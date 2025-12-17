/**
 * Lógica del Dashboard Financiero - Actualizado con Categorías y Chart.js
 */

// 1. UTILIDADES Y CONFIGURACIÓN
const currencyFormatter = new Intl.NumberFormat('es-ES', {
    style: 'currency',
    currency: 'EUR'
});

const dateFormatter = (fechaString) => {
    if (!fechaString) return 'N/A';
    const [year, month, day] = fechaString.split('-');
    return `${day}/${month}/${year}`;
};

function showToast(message) {
    const toastEl = document.getElementById('successToast');
    const toastBody = toastEl.querySelector('.toast-body');
    toastBody.textContent = message;
    const toast = new bootstrap.Toast(toastEl);
    toast.show();
}

// Configuración de Tokens CSRF para Spring Security
const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

const originalFetch = window.fetch;
window.fetch = function() {
    let args = Array.from(arguments);
    let options = args[1] || {};
    if (!options.method) options.method = 'GET';
    if (options.method.toUpperCase() !== 'GET') {
        if (!options.headers) options.headers = {};
        if (options.headers instanceof Headers) {
            options.headers.append(csrfHeader, csrfToken);
            options.headers.append('Content-Type', 'application/json');
        } else {
            options.headers[csrfHeader] = csrfToken;
            if(!options.headers['Content-Type']) options.headers['Content-Type'] = 'application/json';
        }
    }
    args[1] = options;
    return originalFetch.apply(this, args);
};


// 2. OBJETO PRINCIPAL DE LA APLICACIÓN
const DashboardApp = {

    // Variable para almacenar la instancia del gráfico y poder actualizarla
    chartInstance: null,

    init: function() {
        this.setFechaActual();
        this.cargarStats();
        this.cargarIngresos();
        this.cargarGastos();
        this.cargarGrafico(); // <--- NUEVO: Cargar el gráfico al inicio
        this.bindEvents();
    },

    setFechaActual: function() {
        const opciones = { year: 'numeric', month: 'long', day: 'numeric' };
        document.getElementById('currentDateDisplay').textContent = new Date().toLocaleDateString('es-ES', opciones);
    },

    // --- CARGA DE DATOS ---

    cargarStats: function() {
        fetch('/api/dashboard/stats')
            .then(res => res.json())
            .then(data => {
                document.getElementById('totalIngresosDisplay').textContent = currencyFormatter.format(data.ingresosMes);
                document.getElementById('gastosMesDisplay').textContent = currencyFormatter.format(data.gastosMes);

                const balanceEl = document.getElementById('balanceMesDisplay');
                balanceEl.textContent = currencyFormatter.format(data.balanceTotalUsuario);

                if (data.balanceTotalUsuario < 0) {
                     balanceEl.classList.remove('text-white');
                     balanceEl.style.color = '#ffb3b3';
                } else {
                     balanceEl.style.color = '#ffffff';
                }
            })
            .catch(err => console.error("Error stats:", err));
    },

    // --- NUEVA FUNCIÓN: CARGAR GRÁFICO ---
    cargarGrafico: function() {
        // Verificar si existe el canvas en el HTML antes de intentar dibujar
        const canvas = document.getElementById('gastosChart');
        if (!canvas) return;

        fetch('/api/dashboard/chart-data')
            .then(res => res.json())
            .then(response => {
                const ctx = canvas.getContext('2d');

                // Si ya existe un gráfico previo, lo destruimos para no sobreponerlos
                if (this.chartInstance) {
                    this.chartInstance.destroy();
                }

                // Si no hay datos, podríamos dejarlo vacío o mostrar algo,
                // pero Chart.js maneja arrays vacíos bien (simplemente no pinta nada).

                this.chartInstance = new Chart(ctx, {
                    type: 'doughnut',
                    data: {
                        labels: response.labels,
                        datasets: [{
                            label: 'Gastos (€)',
                            data: response.data,
                            backgroundColor: [
                                '#FF6384', // Rojo (Comida/Super)
                                '#36A2EB', // Azul (Vivienda)
                                '#FFCE56', // Amarillo (Ocio)
                                '#4BC0C0', // Verde (Salud)
                                '#9966FF', // Violeta (Transporte)
                                '#FF9F40', // Naranja (Otros)
                                '#C9CBCF'  // Gris
                            ],
                            borderWidth: 2,
                            borderColor: '#ffffff'
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false, // Permite ajustar altura con CSS si es necesario
                        plugins: {
                            legend: {
                                position: 'right',
                            },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        let label = context.label || '';
                                        if (label) {
                                            label += ': ';
                                        }
                                        if (context.parsed !== null) {
                                            label += new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR' }).format(context.parsed);
                                        }
                                        return label;
                                    }
                                }
                            }
                        }
                    }
                });
            })
            .catch(err => console.error("Error cargando gráfico:", err));
    },

    cargarIngresos: function() {
        fetch('/ingresos')
            .then(res => res.json())
            .then(data => {
                const tbody = document.getElementById('ingresosTableBody');
                tbody.innerHTML = '';
                if (data.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="5" class="text-center py-3 text-muted">No hay ingresos registrados.</td></tr>';
                    return;
                }
                data.forEach(ingreso => {
                    const nombreUsuario = ingreso.usuario ? ingreso.usuario.nombre : 'Desconocido';
                    // Si añadiste categoría a ingresos, podrías mostrarla aquí, por ahora uso fuente
                    const row = `
                        <tr>
                            <td class="ps-4">
                                <span class="badge bg-light text-dark border shadow-sm">
                                    <i class="fas fa-user-circle me-1 text-secondary"></i> ${nombreUsuario}
                                </span>
                            </td>
                            <td class="fw-bold text-success">${ingreso.fuente}</td>
                            <td>${dateFormatter(ingreso.fecha)}</td>
                            <td class="text-end fw-bold">${currencyFormatter.format(ingreso.cantidad)}</td>
                            <td class="text-center">
                                <button class="btn btn-sm btn-outline-primary me-1" onclick="DashboardApp.abrirEditarIngreso(${ingreso.id})">
                                    <i class="fas fa-pen"></i>
                                </button>
                                <button class="btn btn-sm btn-outline-danger" onclick="DashboardApp.eliminarIngreso(${ingreso.id})">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </td>
                        </tr>`;
                    tbody.innerHTML += row;
                });
            });
    },

    cargarGastos: function() {
        fetch('/gastos')
            .then(res => res.json())
            .then(data => {
                const tbody = document.getElementById('gastosTableBody');
                tbody.innerHTML = '';
                if (data.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="5" class="text-center py-3 text-muted">No hay gastos registrados.</td></tr>';
                    return;
                }
                data.forEach(gasto => {
                    const nombreUsuario = gasto.usuario ? gasto.usuario.nombre : 'Desconocido';
                    // Manejo seguro de la categoría por si es null (datos antiguos)
                    const categoria = gasto.categoria ? gasto.categoria : 'General';

                    const row = `
                        <tr>
                            <td class="ps-4">
                                <span class="badge bg-light text-dark border shadow-sm">
                                    <i class="fas fa-user-circle me-1 text-secondary"></i> ${nombreUsuario}
                                </span>
                            </td>
                            <td>
                                <div class="fw-bold text-danger">${gasto.descripcion}</div>
                                <small class="text-muted"><i class="fas fa-tag me-1"></i>${categoria}</small>
                            </td>
                            <td>${dateFormatter(gasto.fecha)}</td>
                            <td class="text-end fw-bold text-danger">-${currencyFormatter.format(gasto.cantidad)}</td>
                            <td class="text-center">
                                <button class="btn btn-sm btn-outline-primary me-1" onclick="DashboardApp.abrirEditarGasto(${gasto.id})">
                                    <i class="fas fa-pen"></i>
                                </button>
                                <button class="btn btn-sm btn-outline-danger" onclick="DashboardApp.eliminarGasto(${gasto.id})">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </td>
                        </tr>`;
                    tbody.innerHTML += row;
                });
            });
    },

    // --- ACCIONES DE GUARDADO ---

    guardarIngreso: function() {
        const data = {
            fuente: document.getElementById('ingresoFuente').value,
            cantidad: parseFloat(document.getElementById('ingresoCantidad').value),
            fecha: document.getElementById('ingresoFecha').value
            // Si en el futuro añades <select id="ingresoCategoria">, agrégalo aquí:
            // categoria: document.getElementById('ingresoCategoria')?.value
        };

        if (!data.fuente || !data.cantidad || data.cantidad <= 0 || !data.fecha) {
            Swal.fire('Error', 'Datos incorrectos.', 'warning');
            return;
        }

        fetch('/ingresos', { method: 'POST', body: JSON.stringify(data) })
            .then(res => {
                if (res.ok) {
                    $('#modal-nuevo-ingreso').modal('hide');
                    document.getElementById('form-nuevo-ingreso').reset();
                    DashboardApp.refreshAll();
                    showToast('Ingreso guardado');
                }
            });
    },

    guardarGasto: function() {
        // Obtenemos el valor del select de categoría
        const categoriaSelect = document.getElementById('gastoCategoria');
        const categoriaValor = categoriaSelect ? categoriaSelect.value : 'Otros';

        const data = {
            descripcion: document.getElementById('gastoDescripcion').value,
            cantidad: parseFloat(document.getElementById('gastoCantidad').value),
            fecha: document.getElementById('gastoFecha').value,
            categoria: categoriaValor // <--- AÑADIDO
        };

        if (!data.descripcion || !data.cantidad || data.cantidad <= 0 || !data.fecha) {
            Swal.fire('Error', 'Datos incorrectos. Introduce cantidad positiva.', 'warning');
            return;
        }

        fetch('/gastos', { method: 'POST', body: JSON.stringify(data) })
            .then(res => {
                if (res.ok) {
                    $('#modal-nuevo-gasto').modal('hide');
                    document.getElementById('form-nuevo-gasto').reset();
                    DashboardApp.refreshAll(); // Esto recargará el gráfico también
                    showToast('Gasto guardado');
                }
            });
    },

    // --- ACCIONES DE ELIMINACIÓN ---

    eliminarIngreso: function(id) {
        Swal.fire({
            title: '¿Eliminar?',
            text: "Se restará del saldo.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Sí, eliminar'
        }).then((result) => {
            if (result.isConfirmed) {
                fetch(`/ingresos/${id}`, { method: 'DELETE' }).then(res => {
                    if (res.ok) { DashboardApp.refreshAll(); showToast('Ingreso eliminado'); }
                });
            }
        });
    },

    eliminarGasto: function(id) {
        Swal.fire({
            title: '¿Eliminar Gasto?',
            text: "El importe volverá a tu saldo.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Sí, eliminar',
            confirmButtonColor: '#d33'
        }).then((result) => {
            if (result.isConfirmed) {
                fetch(`/gastos/${id}`, { method: 'DELETE' }).then(res => {
                    if (res.ok) { DashboardApp.refreshAll(); showToast('Gasto eliminado'); }
                });
            }
        });
    },

    // --- ACCIONES DE EDICIÓN ---

    abrirEditarIngreso: function(id) {
        fetch(`/ingresos/${id}`)
            .then(res => res.json())
            .then(data => {
                document.getElementById('ingresoIdEditar').value = data.id;
                document.getElementById('ingresoFuenteEditar').value = data.fuente;
                document.getElementById('ingresoCantidadEditar').value = data.cantidad;
                document.getElementById('ingresoFechaEditar').value = data.fecha;
                $('#modal-editar-ingreso').modal('show');
            });
    },

    actualizarIngreso: function() {
        const id = document.getElementById('ingresoIdEditar').value;
        const data = {
            id: id,
            fuente: document.getElementById('ingresoFuenteEditar').value,
            cantidad: parseFloat(document.getElementById('ingresoCantidadEditar').value),
            fecha: document.getElementById('ingresoFechaEditar').value
        };

        fetch(`/ingresos/${id}`, { method: 'PUT', body: JSON.stringify(data) })
            .then(res => {
                 if (res.ok) {
                    $('#modal-editar-ingreso').modal('hide');
                    DashboardApp.refreshAll();
                    showToast('Ingreso actualizado');
                }
            });
    },

    abrirEditarGasto: function(id) {
        fetch(`/gastos/${id}`)
            .then(res => res.json())
            .then(data => {
                document.getElementById('gastoIdEditar').value = data.id;
                document.getElementById('gastoDescripcionEditar').value = data.descripcion;
                document.getElementById('gastoCantidadEditar').value = data.cantidad;
                document.getElementById('gastoFechaEditar').value = data.fecha;

                // Si añadiste el select en el modal de edición, descomenta esto:
                // if(document.getElementById('gastoCategoriaEditar')) {
                //     document.getElementById('gastoCategoriaEditar').value = data.categoria || 'Otros';
                // }

                $('#modal-editar-gasto').modal('show');
            });
    },

    actualizarGasto: function() {
        const id = document.getElementById('gastoIdEditar').value;

        // Intentar coger categoría del modal editar, si no existe, mandar null (el back lo maneja o mantiene el anterior)
        const catEdit = document.getElementById('gastoCategoriaEditar');

        const data = {
            id: id,
            descripcion: document.getElementById('gastoDescripcionEditar').value,
            cantidad: parseFloat(document.getElementById('gastoCantidadEditar').value),
            fecha: document.getElementById('gastoFechaEditar').value,
            categoria: catEdit ? catEdit.value : null
        };

        fetch(`/gastos/${id}`, { method: 'PUT', body: JSON.stringify(data) })
            .then(res => {
                 if (res.ok) {
                    $('#modal-editar-gasto').modal('hide');
                    DashboardApp.refreshAll();
                    showToast('Gasto actualizado');
                }
            });
    },

    enviarBizum: function() {
            // Obtenemos el valor del select de categoría de Bizum
            const categoriaSelect = document.getElementById('bizumCategoria');
            const categoriaValor = categoriaSelect ? categoriaSelect.value : 'Bizum';

            const data = {
                emailDestinatario: document.getElementById('bizumEmail').value,
                cantidad: parseFloat(document.getElementById('bizumCantidad').value),
                concepto: document.getElementById('bizumConcepto').value,
                categoria: categoriaValor // <--- AÑADIDO
            };

            if (!data.emailDestinatario || !data.cantidad || data.cantidad <= 0 || !data.concepto) {
                Swal.fire('Atención', 'Revisa el email, la cantidad (positiva) y el concepto.', 'warning');
                return;
            }

            const btn = document.getElementById('btnEnviarBizum');
            btn.disabled = true;
            btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Enviando...';

            fetch('/bizum/enviar', {
                method: 'POST',
                body: JSON.stringify(data)
            })
            .then(res => res.json().then(d => ({ status: res.status, body: d })))
            .then(response => {
                if (response.status === 200) {
                    $('#modal-enviar-bizum').modal('hide');
                    document.getElementById('form-enviar-bizum').reset();

                    // Actualizamos todo: saldos, listas y el gráfico
                    DashboardApp.refreshAll();

                    Swal.fire('¡Enviado!', response.body.message, 'success');
                } else {
                    Swal.fire('Error', response.body.message || 'Error al enviar Bizum', 'error');
                }
            })
            .catch(err => {
                console.error(err);
                Swal.fire('Error', 'Error de conexión con el servidor', 'error');
            })
            .finally(() => {
                btn.disabled = false;
                btn.innerHTML = '<i class="fas fa-paper-plane me-1"></i> Enviar Dinero';
            });
        },

    refreshAll: function() {
        this.cargarStats();
        this.cargarIngresos();
        this.cargarGastos();
        this.cargarGrafico(); // <--- IMPORTANTE: Recargar gráfico también
    },

    bindEvents: function() {
        document.getElementById('btnGuardarIngreso')?.addEventListener('click', () => this.guardarIngreso());
        document.getElementById('btnGuardarGasto')?.addEventListener('click', () => this.guardarGasto());
        document.getElementById('btnActualizarIngreso')?.addEventListener('click', () => this.actualizarIngreso());
        document.getElementById('btnActualizarGasto')?.addEventListener('click', () => this.actualizarGasto());
        document.getElementById('btnEnviarBizum')?.addEventListener('click', () => this.enviarBizum());
    }
};

document.addEventListener('DOMContentLoaded', () => {
    DashboardApp.init();
});