Este es un proyecto que tenía como objetivo medir distintos gases contaminantes mediante unos sensores ubicados en taxis, programados por nosotros en Arduino, y mostrar en una página web un mapa de contaminación, así como gestionar el acceso a la web mediante distintos tipos de usuarios, quienes podían poseer uno, varios o ningún sensor.

Al ser relativamente grande, fue separado en varios repositorios formando una organización en GitHub, aquí está su enlace:
https://github.com/Poluzone

Yo me dediqué sobre todo al front-end de la página de administración de sensores y usuarios, con la ayuda de Bootstrap, así como algo de Arduino, contribuyendo en todos los archivos.

En la carpeta “Vista_Rapida” podéis encontrar un corto video (disculpad por la mala calidad) grabado durante el desarrollo del proyecto que muestra la página Administración creada por mi con los siguientes elementos:
-	Barra superior para seleccionar si ver la lista de Sensores o de Usuarios.
-	Contadores de sensores tanto alquilados, como averiados como en stock de la supuesta empresa, si está seleccionado SENSORES; y contador de usuarios tanto “normales” como “conductores”, que serían los que dispondrían de los sensores, si está seleccionado USUARIOS.
-	Lista de Sensores disponibles en la base de datos y sus características, así como la posibilidad de editar esta información o borrar dichos sensores, si está seleccionado SENSORES; y lo mismo para usuarios en el caso de estar seleccionado USUARIOS.
-	Información del sensor/usuario seleccionado.
-	Mapa con las lecturas de los sensores (creado por otro compañero pero colocado por mi)

(Es posible que hayan algunos nombres de commits o comentarios que puedan ser inapropiados, no los tengáis en cuenta por favor :) )
