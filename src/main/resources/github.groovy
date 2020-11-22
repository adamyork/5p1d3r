def map = [:]
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
map["uri"] = document.baseUri()
map["taken"] = false
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
if (element.text().contains("We’ve found")) {
    map["taken"] = true
}
return map