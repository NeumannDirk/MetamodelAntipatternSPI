echo "StartBuilding"

cd analyzerApi
call mvn clean install
echo "API done!"
cd ..
cd analyzerImpl
call mvn package
echo "IMPL done!"
cd ..
cd analyzerImplAdvanced
call mvn package
echo "IMPL2 done!"
cd ..
cd mainanalyzer
call mvn package
echo "Main done!"
cd ..

pause