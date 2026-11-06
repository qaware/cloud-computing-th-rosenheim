# Exercise 6. Persistent Volumes (Bonus)

Infos:

- [Cheat-Sheet](cheat-sheet.md)

Tasks:

1. Create a
   [Persistent Volume Claim](https://kubernetes.io/docs/concepts/storage/persistent-volumes/#reserving-a-persistentvolume)
   for a new volume.
2. Bind the persistent volume claim to a volume and mount it in the
   container of the `Hello-Service` app. See
   [Claims as Volumes](https://kubernetes.io/docs/concepts/storage/persistent-volumes/#reserving-a-persistentvolume)
3. Open a shell in the container via `k9s`.
    1. Check that the volume has been mounted in the desired location.
    2. Place a file in the volume.
4. Test whether the file in this volume survives a container restart.
