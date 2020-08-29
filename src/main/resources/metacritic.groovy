def arr = []
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
arr.add(element.tagName())
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
arr.add(element.baseUri())
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
arr.add(element.select(".score_wrapper div").text())
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
arr.add(element.select(".title_wrapper div").text())
//noinspection GroovyAssignabilityCheck,GrUnresolvedAccess
arr.add(element.select(".date_wrapper span").text())
return arr.toArray()