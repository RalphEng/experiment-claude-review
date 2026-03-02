echo sort maven pom files with sortpom-maven-plugin

echo switching directory to project root
%~d0
cd %~p0

call  mvn com.github.ekryd.sortpom:sortpom-maven-plugin:sort