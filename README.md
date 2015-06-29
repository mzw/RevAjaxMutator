# RevAjaxMutator

## Compile
> mvn clean compile assembly:single

## Proxy
You must set up Firefox to use 127.0.0.1:8080 as proxy.

To record responses in $dir:
> mkdir $dir

> ./ram.sh proxy -record $dir

To rewrite responses with files in $dir:
> mkdir $dir

> ./ram.sh proxy -rewrite $dir

To filter certain URL and request method,
> ./ram.sh proxy -rewrite $dir -filter $urlprefix $method

For example:
> ./ram.sh proxy -rewrite record -filter ht://www.smugmug.com:80/ POST
Please don't forget ***port number (":80")***.

## Run (normal) test
> ./ram.sh test $testclass

## Generate mutants
$configclass must implement MutateConfiguration
> ./ram.sh mutate $configclass

## Mutation analysis
> ./ram.sh proxy -rewrite $dir

> ./ram.sh analysis $configclass $testclass
