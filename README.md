# Gitlab Introspection

A groovy toolbox for extracting information from Gitlab servers via API.

Right now this tool/script extracts following information from a Gitlab server.
```
repo count: 77
with license: 37
without license: 40
with readme: 33`
```
Additionally this information is also provided per namespace:
```
name space               , with lic, w/o lic,  readme
argupta                  ,        3 ,        3,        3
asmus                    ,        0 ,        1,        0
BioImage Informatics     ,        1 ,        3,        0
bioinfo                  ,        2 ,        0,        2
```

There might be gradle files missing. I don't know enough about gradle to be sure to include everything required.
## Background 

The question came up, of how to assess practises on a Gitlab Server. Raising awareness of (bad) 
practises was also an issue. 

Something similar was done at [University of Würzburg](https://git.physik.uni-wuerzburg.de/Z03/guidesandscripts/blob/master/REST_Intro/intro.md).

## Usage

Run `org.derse.gitlabintrospection.GitlabLicenses.main`

## Configuration

The [main method](src/main/groovy/org/derse/gitlabintrospection/GitlabLicenses.groovy) has a string called `configFile`. Paste 
the file name of you config file in json format there. 

Example config:
```json
{
  "server":"https://gitlab.gwdg.de/",
  "token": ""
}
```
Use a token in order to access non public projects.

## Limitations

There is no content checking going on. The script does not care what the repo holds.

## Software Licenses

There exists an [OSS review toolkit](TODO: https://github.com/heremaps/oss-review-toolkit
). This tool checks projects dependencies and highlights license conflicts.

## Maintainer

* Stephan Janosch, janosch@mpi-cbg.de

## Contributing

Feel free to add issues, fork and/or create merge requests.

## License

This project is licensed under an adapted [MIT License](LICENSE)