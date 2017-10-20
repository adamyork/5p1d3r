package groovy

def map = [:]
//noinspection GroovyAssignabilityCheck
map["tagName"] = element.tagName()
//noinspection GroovyAssignabilityCheck
map["baseUri"] = element.baseUri()
for (attribute in element.attributes()) {
    //noinspection GroovyAssignabilityCheck
    map[attribute.getKey()] = attribute.getValue()
}
return map