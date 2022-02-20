def map = [:]
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
map["tagName"] = element.tagName()
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
map["baseUri"] = element.baseUri()
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
map["text"] = element.text()

//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
def parts = element.baseUri().split("://")
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
def part0 = parts[0]
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
def part1 = parts[1]
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
def domain = part1[0..part1.indexOf("/")-1]
def host = part0 + "://" + domain

map["host"] = host

//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
for (attribute in element.attributes()) {
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
    map[attribute.getKey()] = attribute.getValue()
}

return map