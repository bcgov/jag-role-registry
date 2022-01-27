## Templates to create openshift components related to jag-role-registry api deployment

### Command to execute template
1) Login to OC using login command
2) Run below command in each env. namespace dev/test/prod/tools
   ``oc process -f jag-role-registry.yaml --param-file=jag-role-registry.env | oc apply -f -``


