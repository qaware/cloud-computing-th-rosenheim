# Bonus Übung 6. Persistent Volumes (Bonus)

Infos:

- [Cheat-Sheet](cheat-sheet.md)

Aufgaben:

1. Erstellen Sie einen
  [Persistent Volume Claim](https://kubernetes.io/docs/concepts/storage/persistent-volumes/#reserving-a-persistentvolume)
  für ein neues Volume.
2. Binden Sie den Persistent Volume Claim an ein Volume und stellen Sie es im
  Container der App `Hello-Service` bereit. Siehe
  [Claims as Volumes](https://kubernetes.io/docs/concepts/storage/persistent-volumes/#reserving-a-persistentvolume)
3. Öffnen Sie über `k9s` eine Shell im Container.
   1. Prüfen Sie, dass das Volume an der gewünschten Stelle gemounted wurde.
   2. Legen Sie eine Datei im Volume ab.
4. Testen Sie, ob die Datei in diesem Volume einen Container-Neustart überlebt.


