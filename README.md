
  
# Multi tenant jhipster using mysql poc
This is a poc showing with a jhipser base how to have a multi-tenant applicaiton.
This is working with a limitted connection pool.

It have been inspired from https://github.com/upway/jhipster-multi-tenancy-poc.
I made a fresh new jhipser and adapted the code. The main issue in the original code was (I think) using the sql "USE ...;" instead of using the setCatalog() function as  documented at https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-configuration-properties.html 

I think, when using the 'USE ..' sql, the second time it gets called, it doesn't swicht schema.  With the setCatalog(), as implemented here, it's fully working. 
                                                                                                
Jwt token include the tenant ID and once a user is logged in, if a db connection is needed, it will get one in from the pool and set the 
schema.  

So this is different to have a pool dedicated for each tenant that is not scalable.

The main advantage of this is that in case of multi tenant, you don't have to code any more the tenantId in each table.
You can keep existing application.
If a tenant needs to rollback his db, you can easily get the nightly backup.
In case of db error/corruption, this will probalby limitted to one tenant.                                       

# jhipster
This application was generated using JHipster 4.10.0, you can find documentation and help at [http://www.jhipster.tech/documentation-archive/v4.10.0](http://www.jhipster.tech/documentation-archive/v4.10.0).

