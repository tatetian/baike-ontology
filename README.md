Baike-Ontology
==============

Using Apache Jena, this project construct a large Chinese onotology from Baidu 
Baike data set. 

How to Install Maven
--------------------

This project is managed by Apache Maven. Thus you have to install Maven first. 
To install Maven, run

    sudo apt-get install maven

in Ubuntu or 

    brew install maven

in Mac OS X. For more information about Apache Maven, see http://maven.apache.org.

How to Run
-----------------

Before you run the project, make sure two data files `baidu-taxonomy.dat` and 
`baidu-article.dat` are placed in `data` folder of project directory. 
In addition, this program requires enormous amount of memory. So you should 
set up JVM with a maximum heap size of at least 32GB. 

The main program, which reads data file, constructs the ontology and prints 
the statistics, is written as a unit test. To run it, use the following command

    mvn -Dtest=BaikeOntology test 

