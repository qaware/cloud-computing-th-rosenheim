# Übung: Vertraut werden mit Terraform (Optional)

Führen sie das Interactive Terraform Lab unter https://learn.hashicorp.com/tutorials/terraform/infrastructure-as-code?in=terraform/aws-get-started#quick-start aus.


# Übung: Infrastructure as Code mit Terraform auf AWS

Ziel dieser Übung ist das erlernen grundlegender Infrastructure as Code Fähigkeiten mit Terraform auf der AWS Cloud. Hierzu werden Sie die Architektur aus der ersten IAAS Übung mit Terrform nachbauen. Grundlegende Schritte sind hierfür schon vorbereitet.

### Vorbereiten des Terraform Containers

1. Bauen sie Container im Verzeichnis `iaas-container`
   ```
   docker build . -t iaas-container:v1
   ```
2. Starten Sie den `iaas-container` und mounten Sie das Verzeichnis `terraform` in den `/root/teil-2` im Container. Beispiel mit Bash aus dem Verzeichnis:
   ```
   docker run -v /path/on/host/terraform:/root/terraform -it --rm -w /root iaas-container:v1
   ``` 
3. Konfigurieren Sie wieder Ihren AWS Zungang mit `aws configure`.
   Geben die die vom Übungsleiter bereitgestellten Zugangsdaten ein. 
   Benutzen sie `us-east-1` als Region und den default als Output Format.
   
### Initialisieren von Terraform
1. Initialisieren Sie dann im Verzeichnis `terraform` Terraform mit `terraform init`.
2. Schauen Sie sich die bereits existierenden Terraform Dateien an und machen Sie sich mit der grundlegenden Struktur vertraut.
3. Führen sie `terraform plan` aus. 
   Die Ausgabe zeigt ihnen an, was Terraform bei einem Deployment alles ändern wird.
   Machen sie sich mit der Ausgabe vertraut.
4. Fürhren sie `terraform apply` aus, um die Änderungen zu deployen.   
   
### Erweitern der Terraform Konfiguration
1. Implementieren Sie alle Stellen die mit `#TODO` annotiert sind. 
   Definieren Sie sich selbst eine sinnvolle reihenfolge. 
   In regelmäßigen Abständen sollten Sie ihre Implementierungsarbeiten auf die AWS Cloud anwenden mit `terraform plan` bzw. `terraform apply`.
2. Am Ende sollte die Terraform Konfiguration einen Output Parameter mit einer validen und funktionierenden URL enthalten.
3. Erzeugen Sie einen neuen Workspace mit `terraform workspace new dev`, wechseln zu diesem `terraform workspace select dev` und überprüfen Sie ob Sie mit `terraform apply` eine zweite Umgebung erzeugen können. Wenn nicht passen Sie ihre Konfigurationen so an, dass dies möglich ist. Machen Sie dafür insbesondere Benennungen von Ressourcen abhängig vom verwendeten Workspace.
4. Zerstören Sie alle erzeugten Ressourcen mit `terraform destroy` auf beiden Workspaces.

