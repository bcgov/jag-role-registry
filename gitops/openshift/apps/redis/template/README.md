## Templates to create openshift components related to redis deployment

### Command to execute template
1) Login to OC using login command
2) Run below command in each env. namespace dev/test/prod/tools
   ``oc process -f redis.yml --param-file=redis.env | oc apply -f -``
