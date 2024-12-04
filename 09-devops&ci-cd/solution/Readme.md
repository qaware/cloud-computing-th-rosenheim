# Deployment

Create a new repository on github and push the contents of the `solution` directory.

Then create a personal access token with the proper permissions. 
It needs read-write access for the contents and permission to setup a ssh deploy key. 

With fine grained tokens set the following permissions (not production ready!!!): 
- Administration => read and write
- Contents => read and write

Replace the variables and then run: 
```shell
flux bootstrap github --private=false \
  --personal=true \
  --owner=$your-github-user \
  --repository=$your-repository\
  --branch=main \
  --path=clusters/my-cluster
```

The credentials for the flux dashboard are username `admin` and password `admin`.