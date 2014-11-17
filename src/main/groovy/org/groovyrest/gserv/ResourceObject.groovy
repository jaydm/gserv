package org.groovyrest.gserv

/**
 * Created by lcollins on 11/16/2014.
 */
class ResourceObject extends GServResource {
    List<Closure> resourceDefinitions = []

    ResourceObject(String path) {
        super(path)
    }

    def resource(Closure resourceDef) {
        resourceDefinitions << resourceDef
    }

}
