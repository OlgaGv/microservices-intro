package com.learn.micro.resourceservice.service.impl;

import com.learn.micro.resourceservice.model.UploadResourceResponse;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


import com.learn.micro.resourceservice.entity.ResourceEntity;
import com.learn.micro.resourceservice.exception.GeneralFailureException;
import com.learn.micro.resourceservice.mapper.ResourceMapper;
import com.learn.micro.resourceservice.model.DeleteResourceResponse;
import com.learn.micro.resourceservice.model.GetResourceResponse;
import com.learn.micro.resourceservice.model.ResourceMetadataDto;
import com.learn.micro.resourceservice.repository.ResourceRepository;
import com.learn.micro.resourceservice.service.MessageHelper;
import com.learn.micro.resourceservice.service.ResourceProcessor;
import com.learn.micro.resourceservice.service.ResourceService;

@RequiredArgsConstructor
@Service
public class ResourceServiceImpl implements ResourceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceServiceImpl.class);
    private final ResourceRepository resourceRepository;
    private final RestTemplate restTemplate;
    private final ResourceMapper resourceMapper;
    private final ResourceProcessor resourceProcessor;
    private final MessageHelper messageHelper;
    @Value("${song-service.uri}")
    private String songServiceUri;

    @Override
    public GetResourceResponse findById(String id) {
        if (!isValidId(id)) {
            throw new IllegalArgumentException(
                    MessageFormat.format(messageHelper.getMessage("validation.id.invalid"), id));
        }
        int resourceId = Integer.parseInt(id);
        return resourceRepository.findById(resourceId)
                .map(resourceMapper::mapEntityToResourceContentDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageFormat.format(messageHelper.getMessage("error.resource.not.found"),
                                id)));
    }

    @Override
    @Transactional
    public UploadResourceResponse save(byte[] fileContent) {
        if (!resourceProcessor.isValidMp3(fileContent)) {
            throw new IllegalArgumentException(messageHelper.getMessage("validation.mp3.invalid"));
        }

        ResourceEntity resourceEntity = new ResourceEntity();
        resourceEntity.setResourceContent(fileContent);
        ResourceEntity savedResource = resourceRepository.save(resourceEntity);
        if (savedResource.getId() != null) {
            try {
                ResourceMetadataDto metadataDto = resourceProcessor.getResourceMetadata(fileContent);
                metadataDto.setId(savedResource.getId());
                ResponseEntity<?> response = sendMetadata(metadataDto);
                if (response.getStatusCode() == HttpStatus.OK) {
                    return resourceMapper.mapEntityToUploadResourceDto(savedResource);
                }
            } catch (RestClientException e) {
                throw new GeneralFailureException(messageHelper.getMessage("server.error.general"));
            }
        }
        throw new GeneralFailureException(messageHelper.getMessage("server.error.general"));
    }

    @Override
    @Transactional
    public DeleteResourceResponse delete(String ids) {
        List<Integer> idsToDelete = parseAndValidateIds(ids);
        List<Integer> deletedIds = new ArrayList<>();
        for (Integer id : idsToDelete) {
            if (resourceRepository.existsById(id)) {
                resourceRepository.deleteById(id);
                deletedIds.add(id);
            }
        }
        if (!deletedIds.isEmpty()) {
            deleteMetadata(deletedIds);
        }
        return new DeleteResourceResponse(deletedIds);
    }

    private void validateIds(String ids) {
        if (ids.length() >= 200) {
            throw new IllegalArgumentException(
                MessageFormat.format(messageHelper.getMessage("validation.ids.length"), ids.length()));
        }
        if (!ids.matches("^\\d+(,\\d+)*$")) {
            throw new IllegalArgumentException(
                    messageHelper.getMessage("validation.ids.invalid"));
        }
    }

    private boolean isValidId(String id) {
        return id.matches("^[1-9]\\d*$"); // Only allows numeric IDs
    }

    private List<Integer> parseAndValidateIds(String idParam) {
        validateIds(idParam);
        return Arrays.stream(idParam.split(","))
                .map(String::trim)
                .filter(id -> !id.isEmpty())
                .map(Integer::parseInt)
                .filter(id -> id > 0)
                .toList();
    }

    private ResponseEntity<?> sendMetadata(ResourceMetadataDto songMetadata) {
        LOGGER.info("Saving song metadata to song-db using URI {}", songServiceUri);
        return restTemplate.postForEntity(songServiceUri + "/songs", songMetadata, ResourceMetadataDto.class);
    }

    private void deleteMetadata(List<Integer> deletedIds) {
        String idToDeleteMetadata = deletedIds
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("id", idToDeleteMetadata);
        LOGGER.info("Deleting song metadata from song-db using URI {}", songServiceUri);
        restTemplate.delete(songServiceUri + "/songs?id={id}", urlParams);
    }
}
