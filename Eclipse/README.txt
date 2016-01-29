Eclipse Ganymede 3.4	eclipse-jee-ganymede-SR1-win32.zip
Eclipse plugins:
	Maven		http://m2eclipse.sonatype.org/update/
	Subclipse	http://subclipse.tigris.org/update_1.6.x
	(HiberObjects	http://objectgeneration.com/update/)

Initialiseren project "HiberObjects":
 - Start eclipse, stel als workspace in de subdirectory IDE van het project
 - Kies "Import..." en dan "SVN => Check out Projects from SVN"
 - Creëer repository location
 	svn://mijn.valori.nl/afhandeling
 	svn://mijn.valori.nl/dashboard
 	user+password rob+ovenschotel
 - Negeer eventuele warnings over unsupported features
 - Selecteer folder "IDE/HiberObjects" en klik op "Finish"
 - Kies "OK" bij een eventuele "Confirm Overwrite" popup

Klap het onderste HiberObjects-icon uit om het "Model" te openen.
LET OP: doe dit in de 'Java EE' of de 'HiberObjects' perspective,
want het uitklappen KAN NIET IN DE JAVA PERSPECTIVE!