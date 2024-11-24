#!/bin/bash
image=docker.io/devopsaes/ocrdms-be:newdev-0.0.5
docker build --no-cache -t $image .
echo "___________________________image built"
docker push $image
echo "___________________________image pushed"