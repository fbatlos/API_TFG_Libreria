# APP Tareas

## Descripción

La aplicación es un gestor de tareas con todas sus funcionalidades, además de añadirle la separación de las dos entidades usuario y administrador, la cual consta de los siguientes documentos/colecciones:

### 1º Usuario
- **username:** String → es el nombre del usuario  
- **email:** String → es el correo usado para ser registrado  
- **password:** String → es la contraseña del usuario  
- **dirección:** Direccion → es la dirección del usuario  
- **roles:** String? → es la entidad que representa la persona registrada  

### 2º Dirección
Todos los datos se sobreentienden en este apartado.  
- **calle:** String  
- **num:** String  
- **municipio:** String  
- **provincia:** String  
- **cp:** String  

### 3º Tareas
- **titulo:** String → es el título de la tarea a realizar.  
- **cuerpo:** String → es el cuerpo especificando la tarea.  
- **username:** String → es el usuario al que pertenece la tarea.  
- **fecha_pub:** Date → es el día y la hora que se publicó la tarea  
- **completada:** Bool → estará en `true` si es completada y en `false` si no.  

---

## Endpoints

### 1º Usuario:
La idea sería tener un CRUD.  

- **GET** `/usuario` → Obtenemos el usuario y si es admin podrá ver todos los usuarios.  
- **POST** `/login` → Se inicia sesión con MongoDB con la contraseña encriptada y retornará el token.  
- **POST** `/register` → Se registrará dentro de MongoDB con todos los datos.  
- **PUT** `/usuario` → El propio usuario podrá actualizar sus datos cuando quiera.  
- **DELETE** `/usuario` → El propio usuario podrá eliminar su cuenta y el admin podrá eliminar la que quiera.  

### 2º Tareas:
La idea sería tener un CRUD que haga distinción de tu rol.  

- **GET** `/tareas` → Obtenemos todas las tareas del usuario y el admin puede ver todas.  
- **POST** `/tarea` → Insertamos una tarea con el `username` del usuario correspondiente o, si es admin, pediremos el nombre del usuario a asignar esa tarea.  
- **PUT** `/tarea` → Actualizará la tarea asignada.  
- **DELETE** `/tarea` → El usuario eliminará sus tareas y el admin podrá eliminar todas las tareas.  

---

## Lógica de negocio

### 1º Usuario
El usuario tendrá varias comprobaciones ya que no permitiré:  
- Que se repita el `username` de usuario ni que te puedas registrar con el mismo `email`.  
- La contraseña tiene que ser mayor a 5 caracteres.  
