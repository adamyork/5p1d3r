package groovy

def arr = []
arr.add(element.tagName())
arr.add(element.baseUri())
for (attribute in element.attributes()) {
    arr.add(attribute.getValue())
}
return arr.toArray()