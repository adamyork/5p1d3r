def map = [:]
//noinspection GroovyAssignabilityCheck
map["uri"] = document.baseUri()
map["taken"] = false
if (element.text().contains("We’ve found")) {
    map["taken"] = true
}
return map