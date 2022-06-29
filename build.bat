echo "StartBuilding"

cd analyzerApi
call mvn clean package
echo "API done!"
cd ..
cd analyzerImpl
call mvn package
echo "IMPL done!"
cd ..
cd mainanalyzer
call mvn package
echo "Main done!"
cd ..

pause