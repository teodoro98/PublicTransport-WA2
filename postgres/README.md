#To start a new docker Postgres container:

docker run -d --name wa2-lab03-pg -p 5432:5432 -e POSTGRES_PASSWORD=pwd -e PGDATA=/var/lib/postgresql/data/pgdata -v <absolute_projectpath>\postgres\data:/var/lib/postgresql/data postgres
docker run -d --name wa2-lab03-pg-tv -p 5433:5432 -e POSTGRES_PASSWORD=pwd -e PGDATA=/var/lib/postgresql/data/pgdata -v <absolute_projectpath>\postgres\datatv:/var/lib/postgresql/data postgres
docker run -d --name wa2-lab03-pg-tc -p 5434:5432 -e POSTGRES_PASSWORD=pwd -e PGDATA=/var/lib/postgresql/data/pgdata -v <absolute_projectpath>\postgres\datatc:/var/lib/postgresql/data postgres
docker run -d --name wa2-lab03-pg-py -p 5435:5432 -e POSTGRES_PASSWORD=pwd -e PGDATA=/var/lib/postgresql/data/pgdata -v <absolute_projectpath>\postgres\datapy:/var/lib/postgresql/data postgres

In case port not bind
docker ps -a
docker restart <containerID>

