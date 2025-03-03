# Descripción APP Tareas

La aplicación es un gestor de tareas con todas sus funcionalidades, además de añadirle la separación de las dos entidades usuario y administrador. Consta de los siguientes documentos/colecciones:

## 1. Usuario
- **username:** `String` → Nombre del usuario.
- **email:** `String` → Correo usado para el registro.
- **password:** `String` → Contraseña del usuario.
- **dirección:** `Direccion` → Dirección del usuario.
- **roles:** `String?` → Entidad que representa a la persona registrada.

## 2. Dirección
- **municipio:** `String`
- **provincia:** `String`

## 3. Tareas
- **titulo:** `String` → Título de la tarea a realizar.
- **cuerpo:** `String` → Descripción de la tarea.
- **username:** `String` → Usuario al que pertenece la tarea.
- **fecha_pub:** `Date` → Día y hora en que se publicó la tarea.
- **completada:** `Boolean` → `true` si está completada, `false` si no.

---

# Planteamiento de la Gestión

Se han creado **2 entidades DTO** para facilitar el ingreso de datos en la API.  
- El **Login** retorna un **token**.  
- El **Register** retorna un **AuthResponse** con el token y datos no sensibles del usuario.  

---

# Endpoints a Revisar

## 1. Gestión de Usuario
- **POST `/usuarios/login`**  
  - Recibe un `LoginUsuarioDTO`, lo compara con la BD en MongoDB y retorna un token si es válido.  
- **POST `/usuarios/register`**  
  - Recibe un `UsuarioRegisterDTO` y procesa los datos en la BD junto con la API externa de **GeoAPI** para la dirección.  

## 2. Gestión de Tareas  
El sistema distingue entre roles (`usuario` y `admin`):  

- **GET `/tareas`** → Devuelve todas las tareas del usuario. El admin puede ver todas.  
- **POST `/tarea`** → Crea una tarea con el `username` correspondiente. Si es admin, puede asignar tareas a otros usuarios.  
- **PUT `/tarea`** → Actualiza la tarea asignada.  
- **DELETE `/tarea`** → El usuario elimina sus propias tareas; el admin puede eliminar cualquier tarea.  

---

# Lógica de Negocio

## 1. Usuario  
Restricciones:  
- No se permite repetir `username` ni `email`.  
- La contraseña debe tener más de **5 caracteres**.  
- Se compara la contraseña con su repetición.  

## 2. Dirección  
Se usa una API externa para validar:  
- Que el **municipio** y la **provincia** existan y tengan sentido.  

## 3. Tareas  
Restricciones:  
- El **título** no puede estar vacío.  
- El **cuerpo** no puede estar vacío.   
- **El administrador tiene acceso total** a todas las funciones.  

---

# Restricciones de Seguridad

### 1. Autenticación  
- Todos los endpoints requieren **JWT**, excepto `login` y `register`.  

### 2. Autorización  
- Solo los **administradores** pueden gestionar todas las tareas.  
- Los **usuarios** solo pueden gestionar sus propias tareas.  

### 3. Validación de Datos  
- Se validan entradas para evitar **inyecciones** y errores.  
- Se valida el formato de **email** al registrar usuarios.  

### 4. Control de Acceso  
- Restricciones a nivel de servicio para evitar que un usuario acceda a datos ajenos.  

---

# Excepciones y Códigos de Estado

| Código  | Descripción |
|---------|------------|
| **500** | INTERNAL SERVER ERROR → Error inesperado en el servidor. |
| **400** | BAD REQUEST → Datos inválidos (ej. email mal formado). |
| **401** | UNAUTHORIZED → No autenticado (token inválido o ausente). |
| **403** | FORBIDDEN → Sin permisos para realizar la acción. |
| **404** | NOT FOUND → Recurso no encontrado. |
| **409** | CONFLICT → Conflicto en la BD (ej. usuario ya registrado). |

# Códigos de Estado Buenos

| Código  | Descripción |
|---------|------------|
| **200** | OK → La pertición salió de forma exitosa. |
| **201** | Created → Se usa en post, se creó sin problema en la base de datos (ej. añadir una Tarea). |
| **204** | Not Content → Usado en delete, eliminó de la base de datos lo necesario (ej. eliminar una Tarea). |

---

# PRUEBAS GESTIÓN TAREAS

Se realizarán pruebas con **Insomnia** y una demostración en video con la API en **Render**.

## 1. Register (`POST /usuarios/register`)
### Pruebas no válidas:
- Intento de registro con `username` repetido.

  ![Register](imagenesPruebas/Captura_de_pantalla_2025-02-21_113519.png)

- Intento de registro con `email` ya registrado.

  ![Register](imagenesPruebas/Captura_de_pantalla_2025-02-21_113731.png)
  
- **Formato de email inválido**.

  ![Register](imagenesPruebas/Captura_de_pantalla_2025-02-21_113820.png)
  
- **Campos vacíos**.

   ![Register](imagenesPruebas/Captura_de_pantalla_2025-02-21_113907.png)
  
- **Contraseñas no coinciden**.

   ![Register](imagenesPruebas/Captura_de_pantalla_2025-02-21_113948.png)
  
- **Municipio inexistente o ilógico**.

   ![Register](imagenesPruebas/Captura_de_pantalla_2025-02-21_114220.png)
   
- **Provincia inexistente**.

   ![Register](imagenesPruebas/Captura_de_pantalla_2025-02-21_114319.png)  

### Prueba válida:  
- Registro exitoso, se retorna el **token** y datos no sensibles.  

   ![Register](imagenesPruebas/Captura_de_pantalla_2025-02-21_114435.png)


### Vemos la BD
  ![Register](imagenesPruebas/Captura_de_pantalla_2025-02-21_120846.png)


---

## 2. Login (`POST /usuarios/login`)
### Pruebas no válidas:
- Usuario o contraseña incorrectos.

  ![Login](imagenesPruebas/Captura_de_pantalla_2025-02-21_120527.png)


  ![Login](imagenesPruebas/Captura_de_pantalla_2025-02-21_120545.png)

### Pruebas válidas:
- Credenciales correctas, se obtiene el **token**.

  ![Login](imagenesPruebas/Captura_de_pantalla_2025-02-21_120724.png)

---

## 3. Tareas
  Ahora vamos a realizar las pruebas de Tareas.

  Para ello necesitamos 2 tokens, el priemero de rol usuario y otro con rol administrador.

  ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_130255.png)
  
## Tareas (`GET /tareas/tareas`)
- Obtenermos las tareas de un usuario, ya que es de rol usuario.
  ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_130348.png)

  
- Obtenermos todas las tareas, ya que es de rol administrador.
  ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_130420.png)

## Tareas (`POST /tareas/tarea`)

- Primeramente vamos a probar un rol usuario.
  ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_130518.png)

### Pruebas no válidas:
- Comprobamos que la tarea tiene **título**, **cuerpo** en estos casos estan vacios.
  ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_130531.png)

  ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_130544.png)

- En este caso el usuario no dirá a quien le pone la tarea pero igualmente está controlado por temas de seguridad.

  ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_130738.png)


- Ahora vamos a probar con el rol admin.

  ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_130820.png)
  
- El caso de **título**, **cuerpo** es igual, pero en el caso de poner el nombre del usuario.

  ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_130900.png)
  
### Pruebas válidas:

- Todo está correcto:
  ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_130911.png)

- Comprobamos que se ha creado correctamente usando el token admin en el apartado get.
  ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_130943.png)

## Tareas (`PUT /tareas/tarea`)
- Primeramente vamos a probar un rol usuario.
  ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_131113.png)

### Pruebas no válidas:

- Igual que antes el **título** y el **cuerpo**  son campos obligatorios.
   ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_131337.png)

   ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_131351.png)

- El usuario con rol usuario no podrá actualizar las tareas de otra persona.
  ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_131409.png)

- Los otros campos se podrían actualizar pero son de un formato enconcreto lo cual no deja corte al fallo.

- Ahora probamos con el rol administrador: 

  ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_131600.png)

- Igual que antes el **título** y el **cuerpo**  son campos obligatorios.
    - ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_131614.png)

    - ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_131629.png)

    - ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_131651.png)

- Aquí al ser admin si puede añadir un nombre para alterar la tarea al usurio
  ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_132007.png)
  

### Pruebas válidas:

- El usuario con rol usuario con todos los campos correctos.
  ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_131424.png)

- El administrador a puesto todos los datos correctos.
  ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_132110.png)

- Comprobamos que se ha actualizado con exito.
   ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_132128.png)


## Tareas (`DEL /tareas/tarea/{id}`)
- Como antes vamos a probar primeramente el user.
  ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_132219.png)

### Pruebas no válidas:

  - Intentamos eliminar una tarea que no es suya.
    ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_132219.png)

### Pruebas válidas:

  - El user elimina su tarea.
    ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_132324.png)

  - El Admin elimina cualquier tarea .
    ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_132241.png)

  - Comprobamos que se ha eliminado perfectamente.
    ![Tarea](imagenesPruebas/Captura_de_pantalla_2025-02-26_132259.png)



# Prueba con Interfaz  
Se ejecuta en **Render**, repitiendo las pruebas anteriores. 

![Render](imagenesPruebas/Captura_de_pantalla_2025-02-21_115326.png)

**Video demostrativo**:  
🔗 [Ver video en Google Drive - Login/Register](https://drive.google.com/file/d/1CxVJwtg5QR0ff-aLchzrFr9mpZ9Fxy_r/view?usp=sharing)

🔗 [Ver video en Google Drive - Full Tarea](https://drive.google.com/file/d/1HnSRGRpa5BT9qPMO0g_BtPTYLHvxw5Tn/view?usp=sharing)

  
