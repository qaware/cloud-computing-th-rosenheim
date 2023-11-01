# Übung: Infrastructure as Code mit Terraform auf AWS mit LocalStack

[LocalStack](https://localstack.cloud/) ist eine Software, die unter anderem die AWS Cloud lokal zu Test- und Entwicklungszwecken nachstellt.
Praktischerweise lassen sich die AWS Terraform Module unverändert auch auf LocalStack anwenden. 
Die von Ihnen erstellten Konfigurationen könnten sie also auch unverändert in AWS selbst hochfahren und ausprobieren.

## Voraussetzungen
Wir stellen Ihnen ein voll funktionales Docker Compose setup zur Verfügung mit dem Sie direkt starten können. 
Öffnen Sie ein Terminal und führen sie folgende Kommandos aus: 
```shell
$ cd localstack
$ docker compose up 
```
Der Vorgang kann ein paar Minuten dauern. 
Sie sollten nun einen LocalStack Container am Laufen haben. 
Sie werden feststellen, dass ein Container sofort beendet wurde. 
Dieser Container stellt Ihnen alle notwendigen Tools für die heutige Übung bereit und muss explizit interaktiv gestartet werden.
Öffnen Sie also ein zweites Terminal und führen folgenden Befehl aus: 
```shell
$ docker compose run iaas-container 
```
Sie befinden sich nun als root Nutzer in einer Shell im Container. 
Wechseln Sie im Container in das Verzeichnis `terraform/localstack`.
Dieses Verzeichnis beinhaltet ein vom Host gemountetes Volume mit Terraform Konfigurationsfiles.
Die Konfigurationen sind aber derzeit leer und müssen von Ihnen im Rahmen der Übung erweitert werden. 

## Zielbild
Wir entwickeln in der Übung folgende Infrastruktur: 

Lambda Publisher Funktion → SQS Queue → Lambda Subscriber Funktion → S3 Bucket

Wir entwickeln im Grunde ein Publish/Subscribe System mit Lambda Funktionen. 
Alle Bestandteile werden mit Terraform provisioniert.
Die Lambda Publisher Funktion schreibt bei Ausführung eine Message in die SQS Queue. 
Die Lambda Subscriber Funktion wiederum subscribed auf die SQS Queue und schreibt bei Ausführung die Payload in einen S3 Bucket. 
Der S3 Bucket ist für statisches Website Hosting konfiguriert und sie können sich mit curl dann das Ergebnis in dem S3 Bucket anschauen. 

Sie müssen für diese Übung kein tiefergreifendes Verständnis für Lambda Funktionen mitbringen. 
Die Funktionen selbst werden Ihnen zur Verfügung gestellt. 

## SQS Queue erstellen
Zuerst wollen wir eine SQS Queue erstellen. 
Erstellen Sie hierfür ein file mit Endung `.tf` und fügen die passende resource hinzu: 
```terraform
resource "aws_sqs_queue" "terraform_queue" {
  # TODO
}
```
Konfigurieren Sie die Queue so, dass sie `terraform-example-queue` heißt. 
Hier finden Sie die Dokumentation des Moduls: https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/sqs_queue

Terraform braucht zwangsläufig einen Initialisierungsschritt, bei dem Ressourcen und Module heruntergeladen werden. 
Führen Sie also im Container folgenden Befehl aus: 
```shell
$ tflocal init
```
> __INFO__  
> `tflocal` ist ein dünner wrapper um terraform selbst, damit einfacher mit localstack gearbeitet werden kann. 
> Normalerweise würden sie `terraform` direkt ausführen. 

Nun wollen wir prüfen, welche Ressourcen Terraform für die gegebene Konfiguration erstellen möchte:
```shell
$ tflocal plan
```

Wenn Sie mit dem Ergebnis zufrieden sind, erstellen Sie die Ressourcen:
```shell
$ tflocal apply
```

Prüfen Sie, dass eine Queue angelegt wurde: 
```shell
$  awslocal sqs list-queues --endpoint-url http://localstack:4566
```
Sie sollten folgenden Output erhalten: 
```json
{
    "QueueUrls": [
        "http://localstack:4566/000000000000/terraform-example-queue"
    ]
}

```

> __INFO__  
> `awslocal` ist ein dünner wrapper um die AWS CLI um leichter mit Localstack zu arbeiten. 
> Der Parameter --endpoint-url ist auch nur für unser lokales Localstack setup relevant.

## Lambda Publisher erstellen
Nun wollen wir die Lambda Funktion erzeugen, die in die eben erstellte SQS Queue schreibt. 
Lambda Funktionen sind im Grunde von AWS gehosteter, ausführbarer Code. 
Bei Interesse, können Sie sich den Code im [src](./terraform/localstack/src) directory anschauen. 
Unser Ziel ist es, diesen Code mit Terraform zu provisionieren. 

Verwenden Sie hierfür folgende Konfiguration: 
```terraform
module "lambda_publisher" {
  source = "terraform-aws-modules/lambda/aws"

  function_name = "publisher"
  description   = "My awesome lambda publisher"
  handler       = "lambda-publisher.lambda_handler"
  runtime       = "python3.8"

  source_path = "./src/lambda-publisher.py"

  attach_policy_json = true
  policy_name = "write_to_sqs_policy"
  policy_json = <<POLICY
{
  "Version": "2012-10-17",
  "Statement": {
      "Effect": "Allow",
      "Action": "sqs:SendMessage",
      "Resource": "${TODO}" 
  }
}
POLICY

  tags = {
    Name = "lambda-publisher"
  }
}
```

Ersetzen sie das `TODO` mit der ARN der SQS Queue. 
Referenzieren Sie dafür die SQS Queue Resource.

> __INFO__   
> ARN - Amazon Resource Name  
> Die ARN ist eine eindeutige Referenz in AWS.

> __INFO__
> Damit auf AWS ein Service mit einem anderen AWS Service kommunizieren darf, müssen entsprechende Rechte vergeben werden.
> Die Policy erlaubt der Lambda-Funktion genau in diese eine SQS Queue Nachrichten zu schreiben.
> Im Hintergrund passiert in dem Lambda Modul allerdings mehr, was das möglich macht.
> Eine Einführung in das AWS IAM System würde aber wohl den Rahmen der Übung sprechen.

Führen Sie die erstellte Lambda Funktion aus: 
```shell
$ awslocal lambda invoke --function-name publisher --endpoint-url http://localstack:4566 output.txt
```
Sie sollten folgenden Output im Terminal erhalten: 
```json
{
    "StatusCode": 200,
    "ExecutedVersion": "$LATEST"
}
```
Prüfen Sie zudem den Inhalt des files `output.txt`.

Wenn alles geklappt hat, können Sie nun mit folgendem Befehl die Message aus der SQS Queue abholen: 
```shell
awslocal sqs receive-message --queue-url http://localhost:4566/000000000000/terraform-example-queue --endpoint-url http://localstack:4566
```

## Lambda Subscriber erstellen
Wir wollen nun den Subscriber erzeugen. 
Dafür nutzen Sie folgende Konfiguration: 
```terraform
module "lambda_receiver" {
  source = "terraform-aws-modules/lambda/aws"

  function_name = "lambda-receiver"
  handler       = "lambda-receiver.lambda_handler"
  runtime       = "python3.8"

  source_path = "./src/lambda-receiver.py"

  attach_policy_json = true
  policy_name = "receive_from_sqs_policy"
  policy_json = <<POLICY
{
  "Version": "2012-10-17",
  "Statement": {
      "Effect": "Allow",
      "Action": "sqs:ReceiveMessage",
      "Resource": "${TODO}"
  }
}
POLICY

  tags = {
    Name = "lambda-receiver"
  }
}
```

Nachdem der Subscriber erzeugt wurde, wird aber erstmal noch nichts passieren. 
Die Lambda Funktion muss erst noch mit der SQS Queue "verdrahtet" werden.
Verwenden Sie hierfür die [aws_lambda_event_source_mapping](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/lambda_event_source_mapping) resource.

Wenn Sie alles richtig provisioniert haben, liest jetzt zwar der subscriber aus der SQS queue, aber das Resultat wird nicht erfolgreich geschrieben. 

## S3 Bucket erstellen
Erstellen Sie einen S3 bucket der für statisches website hosting eingerichtet ist. 
Nutzen Sie dafür die beiden resources [aws_s3_bucket](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket) und [aws_s3_bucket_website_configuration](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/s3_bucket_website_configuration).

__Wichtig__: Der Bucket muss `result-bucket` heißen. 
S3 Buckets werden beim website hosting über den Namen angesprochen, auch in AWS selbst.
Bucketnamen müssen global einzigartig sein, wodurch Sie den Namen auch als Domain verwenden können. 
In unserem Localstack setup ist der Namen kritisch, da die DNS Auflösung nur unter diesem Namen funktioniert. 

Wenn Sie den Bucket richtig erstellt haben, sollte Folgendes funktionieren und ein Ergebnis liefern:
```shell
$ awslocal lambda invoke --function-name publisher --endpoint-url http://localstack:4566 output.txt
$ curl result-bucket.s3.localhost.localstack.cloud:4566/index.json
```

## Cleanup
Zerstören Sie alle erstellten Ressourcen wieder mit: 
```shell
$ tflocal destroy
```