{{- define "microservices.labels" }}
date: "{{ now | date "2006-01-02" }}"
version: "{{ .Chart.AppVersion }}"
{{- end }}
