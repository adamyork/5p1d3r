def map = [:]
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
map["tagName"] = element.tagName()
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
map["baseUri"] = element.baseUri()
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
map["text"] = element.text()
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
for (attribute in element.attributes()) {
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
    map[attribute.getKey()] = attribute.getValue()
}
return map