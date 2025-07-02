--
DELIMITER //
CREATE FUNCTION calcular_merito(p_puntaje DECIMAL(7,3), p_id_escuela INT)
RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_merito INT DEFAULT 0;

    SELECT COUNT(*) + 1 INTO v_merito
    FROM resultados r
    WHERE r.idescuela = p_id_escuela
    AND r.puntaje > p_puntaje;

    RETURN v_merito;
END //
DELIMITER ;

--
DELIMITER //
CREATE PROCEDURE actualizar_rankings()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_id INT;
    DECLARE v_puntaje DECIMAL(7,3);
    DECLARE v_escuela INT;

    DECLARE cur CURSOR FOR
        SELECT id, puntaje, idescuela FROM resultados;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO v_id, v_puntaje, v_escuela;
        IF done THEN
            LEAVE read_loop;
        END IF;

        UPDATE resultados
        SET merito = calcular_merito(v_puntaje, v_escuela)
        WHERE id = v_id;
    END LOOP;

    CLOSE cur;
END //
DELIMITER ;


-- 3. FUNCIÓN PARA OBTENER ESTADÍSTICAS POR ESCUELA
DELIMITER //
CREATE FUNCTION estadisticas_escuela(p_id_escuela INT, p_tipo_stat VARCHAR(20))
RETURNS DECIMAL(10,3)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_resultado DECIMAL(10,3) DEFAULT 0;

    CASE p_tipo_stat
        WHEN 'PROMEDIO' THEN
            SELECT AVG(puntaje) INTO v_resultado
            FROM resultados
            WHERE idescuela = p_id_escuela;

        WHEN 'MAXIMO' THEN
            SELECT MAX(puntaje) INTO v_resultado
            FROM resultados
            WHERE idescuela = p_id_escuela;

        WHEN 'MINIMO' THEN
            SELECT MIN(puntaje) INTO v_resultado
            FROM resultados
            WHERE idescuela = p_id_escuela;

        WHEN 'TOTAL' THEN
            SELECT COUNT(*) INTO v_resultado
            FROM resultados
            WHERE idescuela = p_id_escuela;

        WHEN 'APROBADOS' THEN
            SELECT COUNT(*) INTO v_resultado
            FROM resultados
            WHERE idescuela = p_id_escuela AND puntaje >= 11.0;

        ELSE
            SET v_resultado = 0;
    END CASE;

    RETURN COALESCE(v_resultado, 0);
END //
DELIMITER ;


-- 4. FUNCIÓN PARA VALIDAR FORMATO DE RESPUESTAS
DELIMITER //
CREATE FUNCTION validar_respuestas(p_respuestas TEXT)
RETURNS BOOLEAN
DETERMINISTIC
NO SQL
BEGIN
    DECLARE v_longitud INT;
    DECLARE i INT DEFAULT 1;
    DECLARE v_char CHAR(1);

    SET v_longitud = CHAR_LENGTH(p_respuestas);

    -- Verificar longitud mínima y máxima
    IF v_longitud < 60 OR v_longitud > 100 THEN
        RETURN FALSE;
    END IF;

    -- Verificar que solo contenga A, B, C, D, * (nula)
    WHILE i <= v_longitud DO
        SET v_char = UPPER(SUBSTRING(p_respuestas, i, 1));
        IF v_char NOT IN ('A', 'B', 'C', 'D', '*') THEN
            RETURN FALSE;
        END IF;
        SET i = i + 1;
    END WHILE;

    RETURN TRUE;
END //
DELIMITER ;