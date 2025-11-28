# APP_Agenda
📚 Agenda Personal

    Este es un proyecto de aplicación móvil para la gestión personal que incluye tres módulos principales: 
    Agenda de Contactos, Registro de Notas y un Calendario de Actividades  
    interconectado con una base de datos SQLite.

🛠️ Tecnologías Utilizadas

    Lenguaje: Java
    Plataforma: Android Studio
    Base de Datos: SQLite
    Componentes Clave: RecyclerView, CursorAdapter, Activity, CalendarView.

🚀 Módulos y Funcionalidades

    1. 👥 Contactos
        Función: Gestiona la lista de contactos
        CRUD:
            Agregar: Nuevo contacto a través de AgregarContactoActivity.java.
            Buscar: Campo de texto dinámico para filtrar por nombre, número o email.
            Editar: Se accede al formulario de edición (ItemContactoActivity.java) 
            pulsando el botón Editar en la lista.
            Eliminar: Se elimina directamente pulsando el botón Eliminar en la lista.

    2. 📝 Notas
        Función: Permite crear y consultar una lista de notas.
        CRUD:
            Crear/Editar: Ambas acciones se manejan en NotaescribirActivity.java.
            Eliminar: Las notas pueden ser eliminadas directamente desde la lista principal.
    
    3. 📅 Calendario y Actividades
        Función: Módulo de planificación que vincula actividades a fechas específicas.
        Interfaz: Utiliza CalendarView para seleccionar una fecha y un RecyclerView para mostrar las actividades de ese día.
        CRUD:
            Crear/Editar: Se usa ActividadEscribirActivity.java para ingresar los datos.
            Filtrado: La lista se actualiza automáticamente al seleccionar una nueva fecha en el calendario.
            Eliminar: Permite eliminar la actividad desde la lista (item_actividad.xml).

✒️ Autores

        Tiago Zelada
        Yessenia Yanspala