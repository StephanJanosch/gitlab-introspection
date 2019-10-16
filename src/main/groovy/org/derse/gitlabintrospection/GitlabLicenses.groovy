package org.derse.gitlabintrospection

import groovy.json.JsonSlurper
import groovyx.net.http.FromServer
import groovyx.net.http.HttpBuilder

class GitlabLicenses {

    /**
     * Helper extracting the link for the next page if answer is paginated
     * @param linkMap
     * @return
     */
    static def helper1(String linkMap) {
        if (linkMap) {
            String nextRel = linkMap.split(",").find { it.contains('rel="next"') }
            if (nextRel)
                return nextRel.split(";")[0].replaceAll("<", '').replaceAll('>', '').trim()
        }
        return null
    }

    static void main(String[] args) {
//        String configFile = 'config_gwdg_de.json'
        String configFile = 'config_mpi-cbg.json'

        def config = new JsonSlurper().parse(new File(configFile))
        boolean getall = true //dev parameter, if false, get's only first projects "page" from api

        println "CONFIG: $config.server\n"
        String linkHeader

        def result
        def myRequest = HttpBuilder.configure {
            if (config.token) //see https://docs.gitlab.com/ee/api/README.html#personal-access-tokens
                request.headers['Private-Token'] = config.token
        }
        myRequest.get {
            request.uri = config.server + 'api/v4/projects'
            response.success { FromServer fs, Object body ->
//                fs.headers.each {println(it)}
                linkHeader = fs.headers.find { h -> h.key == 'Link' }.value
                result = body
            }
        }
        def nextLink = helper1(linkHeader)
        def projects = []
        result.each { projects += it }

        //loop over all following pages
        while (nextLink && getall) {
            myRequest.get {
                request.raw = nextLink
                response.success { FromServer fs, Object body ->
                    linkHeader = fs.headers.find { h -> h.key == 'Link' }.value
                    result = body
                }
            }
            result.each { projects += it }
            nextLink = helper1(linkHeader)
        }//while end

        projects.sort { it.path_with_namespace }

        def fullProjects = []
        //get full info for each project
        projects.each { project ->
            def responseProject
            myRequest.get {
                request.raw = config.server + 'api/v4/projects/' + project.id + '?license=true'
                response.success { FromServer fs, Object body ->
                    responseProject = body
                }
            }
            fullProjects += responseProject
            println "$responseProject.path_with_namespace, ${responseProject.license?.name}, $responseProject.license_url"
        }

        println ""
        println "repo count: ${fullProjects.size()}"
        println "with license: ${fullProjects.findAll { it.license_url }.size()}"
        println "without license: ${fullProjects.findAll { !it.license_url }.size()}"
        println "with readme: ${fullProjects.findAll { !it.readme_url }.size()}"
        println ""

        printf('%-25s, %8s,%8s,%8s\n',['name space','with lic','w/o lic','readme'])
        fullProjects.namespace.name.unique().each { namespace ->
            def namespaceProjects = fullProjects.findAll { it.namespace.name.equals(namespace) }

            printf('%-25s, %8d , %8d, %8d\n',
                    [namespace,
                     namespaceProjects.findAll {it.license_url}.size(),
                     namespaceProjects.findAll { !it.license_url }.size(),
                     namespaceProjects.findAll {it.readme_url}.size()
                    ])

        }
    }
}
