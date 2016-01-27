import json
import string
import re
import unicodedata
import os

# extracting relevant paragraphs from collected wiki data
# ---------------------ISSUES----------------------------
# {{quote| lalala}} gets deleted
# not able to handle EXTENSIVE NOTES (seen only one instance)
# <sup>2</sup> - what to do?
# single bracket links [http://...] still exist

def find_mentions(para,title):
    # if [[Kumbh Mela]]           -> add Kumbh Mela
    # if [[Kumbh Mela|the Kumbh]] -> add the Kumbh
    mentions = []
    index = para.find('[['+title,0)
    while index != -1:
        close = para.find(']]',index)
        index = para.find('|',index)
        if (index != -1 and index < close):
            mentions.append(para[index+1:close])
        else:
            mentions.append(title)
        index = close
        index = para.find('[['+title,index)
    return mentions

def clean_para(para):
    # TODO: remove all <ref> | remove {{xx}} | replace [[A]] with A | replace [[A|B]] with B
    # NOTE: .*? is non-greedy
    cleaned = re.sub('<br />','',para.encode('utf8'))                                     # remove all <br /> , need utf8 else error
    cleaned = re.sub('&nbsp;',' ',cleaned)                                                # removes &nbsp;
    cleaned = re.sub('&ndash;','-',cleaned)                                               # removes &ndash;
    cleaned = re.sub('<ref[^>]+/>','',cleaned,flags=re.DOTALL)                            # remove <ref .../>
    cleaned = re.sub('<ref.*?</ref>|\{\{.*?\}\}|<!--.*?-->','',cleaned,flags=re.DOTALL)   # remove <ref..>...</ref> {{xx}} | <!--xx-->, flag is to include \n in .*
    cleaned = re.sub('\{|\}','',cleaned,flags=re.DOTALL)                                  # remove extra braces
    #cleaned = re.sub('<ref[^>]+>','',cleaned)              # remove <ref../>
    #cleaned = re.sub('\{\{[^\}]+\}\}','',cleaned)          # remove {{xx}}
    cleaned = re.sub('\\\\u201[34]','-',cleaned)            # replace \u2013 or \2014 with -

    if (re.search("File:|Image:",cleaned)):                 # if image caption of the form \n[[File(or Image):xx.jpg|thumb|400px|lalal [[Ganges|Ganga]] lalalaa]]
        # usually sentence starts with |[A-Z] or |[[
        # assumes paragraph is only \n[[File:....]]
        m = re.search("\|[^\|]+(\[|\])",cleaned)
        cleaned = cleaned[m.start()+1:]
    
    cleaned = re.sub('(?<=\[\[)[^\|\[]+(?=\|)','',cleaned)     # replace [[A|B]] -> [[|B]]
    cleaned = re.sub('\[|\||\]|\\n|\*','',cleaned)     # remove [, |, ], { , } , *, \n
    cleaned = unicodedata.normalize('NFKD',cleaned.decode('unicode_escape')).encode('ascii','ignore')
    #^ convert ascii to equivalent ascii (wasn't resolving \u2014 hyphen)
    
    return cleaned

def extract_from_para(para,title):
    references = []
    mentions   = find_mentions(para,title)
    para       = clean_para(para)
    index      = 0
    for mention in mentions:
        reference = {}
        reference['text']         = para
        reference['mention-text'] = mention
        reference['start']        = para.find(mention,index)
        reference['end']          = reference['start'] + len(mention)    # can call in python using reference['text'][reference['start']:reference['end']]
        # advance index to find all mentions in case a paragraph has multiple mentions with the same mention-text
        index = reference['end']
        references.append(reference)
    return references
    
def extract_from_backlink_text(text,title):
    references = []
    index = text.find('[['+title,0)
    while index != -1:
        para_start = text.rfind("\n",0,index)
        para_end   = text.find("\n",index)
        references.extend(extract_from_para(text[para_start+1:para_end],title))
        index = text.find('[['+title,para_end)  # extract_from_para will take care if multiple in para, hence skip
    
    return references    
    
#               JSON structure
# ======================================
#                   file
#      -------------====-------
#     /      |       |         \ 
#  pgid  pgtitle    type    backlinks
#                          [0  ,  1  , 2, ... 10] --> no. of backlink pages
#                      -----=-----
#                     /     |     \
#                  blid  bltitle  references
#                              [0  , 1  , 2 ...]  --> no. of mentions
#                         ------=-------
#                        /    |    |    \       
#                     text start  end  mention 
#                                        text

###################################################

pageids = [line.rstrip('\n') for line in open('pageids.txt')]
# pageids are strings

for pageid in pageids:
    if os.path.isfile(pageid.strip() + 's.txt'):
        print 'skipped : ' + pageid.strip()
        continue
    
    f = open(pageid.strip() + 's.txt','w')
    g = open(pageid.strip() + '.txt','r')
    pg_data = json.loads(g.read())
        
    if (('error' in pg_data) or (len(pg_data) != 3)):
        print 'skipped : ' + pageid.strip()+ '(error in data)'
        continue
        
    bl_ref  = {}
    bl_ref['page-wiki-id']    = pageid
    bl_ref['page-wiki-title'] = pg_data['parent']['parse']['title']
    bl_ref['backlinks']       = []
    i = 0
#    print('\n===============================')
#    print bl_ref['page-wiki-title']
#    print('-------------------------------\n')
    
    for backlink in pg_data['query']['pages']:
        bl_ref['backlinks'].append({})
        bl_ref['backlinks'][i]['backlink-wiki-id']    = backlink
        bl_ref['backlinks'][i]['backlink-wiki-title'] = pg_data['query']['pages'][backlink]['title']
        
        if ('revisions' in pg_data['query']['pages'][backlink]):
            bl_ref['backlinks'][i]['parent-references']   = extract_from_backlink_text(pg_data['query']['pages'][backlink]['revisions'][0]['*'],bl_ref['page-wiki-title'])
        else:
            continue
            
 #          for ref in bl_ref['backlinks'][i]['parent-references']:
 #              print ref['text']
 #              print ref['mention-text'],ref['start']
 #              print('\n')
        i += 1
    
    f.write(json.dumps(bl_ref))
    f.close()
    print pageid.strip()
print 'EOP'

#print find_mentions('[[Vajrayana|Buddhist Tantra]] lalal Vajrayana [[Vajrayana|nanan]] [[Vajrayana]] [[Vajrayana|lalal]]','Vajrayana')
#print clean_para("\n\nEntertainment can be distinguished from other activities such as [[education]] and [[marketing]] even though they have learned how to use the appeal of entertainment to achieve their different goals. The importance and impact of entertainment is recognised by scholars<ref>For example, the application of psychological models and theories to entertainment is discussed in Part III of {{cite book|last=Bryant|first=Jennings|title=Psychology of Entertainment|year=2006|publisher=Lawrence Erlbaum Associates, Inc|location=Mahwah, New Jersey|isbn=0-8058-5238-7|pages=367\u2013434|author2=Vorderer, Peter}}</ref><ref name=Sayre>{{cite book|ref=CITEREFSayreKing2010|last=Sayre|first=Shay|title=Entertainment and Society: Influences, Impacts, and Innovations (Google eBook)|year=2010|publisher=Routledge|location=Oxon, New York|isbn=0-415-99806-9|edition=2nd|author2=King, Cynthia}} p. 22.</ref> and its increasing sophistication has influenced practices in other fields such as [[museology]].<ref>{{cite book|title=Conservation, Education, Entertainment?|year=2011|publisher=Channel View Publication|isbn=978-1-84541-164-0|editor=Frost, Warwick}}</ref><ref>{{cite book|title=Museum Revolutions|year=2007|publisher=Routledge|location=Oxon, New York|isbn=0-203-93264-1|authors=Macleod, Suzanne; Watson, Sheila|editor=Knell, Simon J.}}</ref>\n\n")