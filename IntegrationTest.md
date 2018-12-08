The integration test is done manually.

## Steps ##:
* query
   a. with configured additions
   b. without
   both hosting providers github, gitlab
* load the data and serialize in into database (one column for example homepage null)
* select queries:
   a. view block
   b. tabular view
* reload a number of repositories after select
* non sql commands
  a. new search
  b. fetch X rows from github
  c. fetch rows from gitlab
* invalid sql command  
* Search scope
  a. default header data
  b. search in header data
  c. search in content
  e. search in both
