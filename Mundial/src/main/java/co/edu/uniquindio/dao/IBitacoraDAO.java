package co.edu.uniquindio.dao;

import co.edu.uniquindio.model.Bitacora;

import java.time.LocalDateTime;
import java.util.List;

public interface IBitacoraDAO {

    int  registrarIngreso(int idUsuario, LocalDateTime ingreso) throws Exception;
    void registrarSalida(int idBitacora, LocalDateTime salida)  throws Exception;
    List<Bitacora> listarPorRango(LocalDateTime desde, LocalDateTime hasta) throws Exception;
    List<Bitacora> listarPorUsuario(int idUsuario) throws Exception;
}
