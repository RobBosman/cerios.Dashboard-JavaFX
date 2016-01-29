@echo off

for /d %%d IN (*) do (
	del /f /q %%d\.classpath
	del /f /q %%d\.project
	rmdir /q /s %%d\.settings
	rmdir /q /s %%d\target
)
