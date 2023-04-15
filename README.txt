define USER_HOME system variable

local db run:
docker run --name postgres-db -e POSTGRES_PASSWORD=pwd -e POSTGRES_USER=ira -e POSTGRES_DATABASE=onlineshop -p 5432:5432 -d postgres