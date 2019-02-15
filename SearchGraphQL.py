import os
import sys
import httplib

def execute(cmd_parts):
    print(' '.join(cmd_parts))
    os.system(' '.join(cmd_parts))

if(len(sys.argv) == 4):
    execute(['/usr/bin/curl -s --write-out %"{http_code}\n" -X POST -H "Content-Type: application/json"',
         '-H "Authorization: token '+ sys.argv[2]+' " -d',
         '\'{"query":"query($search_term:String!,$results:Int!) { search(query:$search_term, type:REPOSITORY, after:\\\"'+sys.argv[3]+'\\\",first:$results) { edges { cursor } nodes { ... on Repository { url }}}}", "variables":{"search_term":"'+sys.argv[1]+'","results":9}}\'',
         
         'https://api.github.com/graphql'
        
         ])
else:
    execute(['/usr/bin/curl -s --write-out %"{http_code}\n" -X POST -H "Content-Type: application/json"',
         '-H "Authorization: token '+ sys.argv[2]+' " -d',
         '\'{"query":"query($search_term:String!,$results:Int!) { search(query:$search_term, type:REPOSITORY, first:$results) { edges { cursor } nodes { ... on Repository {url licenseInfo { key name } stargazers(first:1) { totalCount  } issues(states:OPEN) { totalCount } updatedAt }}}}", "variables":{"search_term":"'+sys.argv[1]+'","results":9}}\'',
         
         'https://api.github.com/graphql'
        
         ])
    

# Search term for repositories
SearchRepos = '{"query":"query { search(query:\\\"$search_term:String!\\\", type:REPOSITORY, first:$first_elements:Int){ nodes { ... on Repository{ $fields  } }  }"}'

def buildRepositorySearchQuery(searchTerm, first_elements, fields):
    variables = ','.join(['"search_term":"'+searchTerm+'"', '"first_elements":'+first_elements, '"fields":"'+fields+'"'])
    
    variables = 'variables{'+variables+'}'
    return SearchRepos+variables

def PostQueryViaHttp(PostBody, AuthorizationToken):
    conn = httplib.HttpsConnection('https://api.github.com/graphql')
    
    header = {"Content-Type": "application/json", "Authorization":"bearer "+AuthorizationToken}
    conn.request('POST', '', PostBody, header)
    response = conn.getresponse()
    
    #if(!(response.status >= 200 && response.status < 300)):
        #not sucessfull
    #    raise 
