# cloud-aws plugin

This plugin exposes the AWS Java SDK as a plugin.  

### Plugin Summary
|      |      |
|------|------|
| Plugin Name    |  macgyver-plugin-cloud-aws   |
| Service Type   |  aws |
| Primary Interface |  [AWSServiceClient](https://github.com/if6was9/macgyver/blob/master/macgyver-plugin-cloud-aws/src/main/java/io/macgyver/plugin/cloud/aws/AWSServiceClient.java)  |


### Configurable Properties

|key|value| optional | description |
|-------------|---|----|-------|
| serviceType | aws | required |
| accessKey   | _aws access key_ | optional | falls back to ```AWSDefaultCredentialsChain``` if omitted |
| secretKey   | _aws secret key_ | optional |falls back to ```AWSDefaultCredentialsChain``` if omitted |
| region      | region to use    | optional | ```us-east-1```, ```us-west-1```, etc. |
