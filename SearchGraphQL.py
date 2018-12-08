import os
import sys
import httplib

def execute(cmd_parts):
    print(' '.join(cmd_parts))
    os.system(' '.join(cmd_parts))

execute(['/usr/bin/curl -X POST -H "Content-Type: application/json"',
         '-H "Authorization: bearer token" -d',
         '\'{"query":"query { search(query:\\\"'+ sys.argv[1]+'\\\", type:REPOSITORY, first:3) { repositoryCount nodes { ... on Repository {description} }}}"}\'',
         
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
    
    if(!(response.status >= 200 && response.status < 300)):
        #not sucessfull
        raise 
