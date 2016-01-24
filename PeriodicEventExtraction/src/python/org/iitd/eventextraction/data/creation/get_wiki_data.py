import urllib2
import json
import string
import os

# finding a wiki page by id, finding pages that link to it, and storing them together

def getPage(url):
    return urllib2.urlopen(url).read()
    
def getTitleFromJSON(info):
    title = json.loads(info)['parse']['title'].encode('utf8')
    return string.join((string.split(title)),'_')

def getBacklinksFromTitle(title,max_bl_count):
    # max_bl_count is the maximum number of backlinks to extract
    page    = getPage("https://en.wikipedia.org/wiki/Special:WhatLinksHere/" + title)
    index   = 0
    count   = 0
    index   = page.find('whatlinkshere-list',index)
    end_ind = index
    end_ind = page.find('</ul>',end_ind)

    backlinks = []

    while (index < end_ind and count < max_bl_count):
        index    = page.find('/wiki/',index)
        temp     = page.find('\"',index)
        backlink = page[index+6:temp]
        # discard 'User: ', 'Special: ', etc.
        if (string.find(backlink,':')==-1):
            backlinks.append(page[index+6:temp])
            count += 1            
        index  = temp
        
    return backlinks
    
def getBacklinkPages(title,max_bl_count):
    # max_bl_count is the maximum number of backlinks to extract
    # returns JSON with all backlink pages for a given pageid
    backlinks = getBacklinksFromTitle(title,max_bl_count)    # ['a_b',c_d'] -> "a_b|c_d" -> multiple queries in one go
    if (backlinks!=[]):
        return json.loads(getPage("https://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=" + string.join(backlinks,'|')))
    return json.loads('{}')
        
###################################################

pageids = [line.rstrip('\n') for line in open('pageids.txt')]
# pageids are strings

for pageid in pageids:
    if os.path.isfile(pageid.strip() + '.txt'):
        print 'skipped : ' + pageid.strip()
        continue
    f = open(pageid.strip() + '.txt','w')
    info = getPage("https://en.wikipedia.org/w/api.php?action=parse&prop=text&pageid=" + pageid.strip() + "&format=json")
    if 'parse' in json.loads(info):
        title    = getTitleFromJSON(info)
        bl_pages = getBacklinkPages(title,10)       # get 10 backlink pages
        bl_pages['parent'] = json.loads(info)
        f.write(json.dumps(bl_pages))
    else:
        f.write(info)
    f.close()
    print pageid.strip()
print 'EOP'
    