def arr = []
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
arr.add(element.tagName())
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
arr.add(element.baseUri())
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
for (attribute in element.attributes()) {
    arr.add(attribute.getValue())
}
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
arr.add(element.text())
return arr.toArray()