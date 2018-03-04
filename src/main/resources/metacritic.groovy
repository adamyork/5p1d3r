def arr = []
arr.add(element.tagName())
arr.add(element.baseUri())
arr.add(element.select(".score_wrapper div").text())
arr.add(element.select(".title_wrapper div").text())
arr.add(element.select(".date_wrapper span").text())
return arr.toArray()