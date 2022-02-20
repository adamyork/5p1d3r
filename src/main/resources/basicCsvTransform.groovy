def arr = []
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
arr.add(element.tagName())
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
arr.add(element.baseUri())

//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
def parts = element.baseUri().split("://")
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
def part0 = parts[0]
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
def part1 = parts[1]
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
def domain = part1[0..part1.indexOf("/")-1]
def host = part0 + "://" + domain

arr.add(host)

//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
for (attribute in element.attributes()) {
    arr.add(attribute.getValue())
}
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
arr.add(element.text())

return arr.toArray()