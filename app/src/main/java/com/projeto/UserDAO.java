package com.projeto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public boolean validarLogin(String user, String pass) {

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user);
            pstmt.setString(2, pass);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {

            return false;

        }

    }

    public List<User> listar() {

        List<User> lista = new ArrayList<>();

        String sql = "SELECT * FROM users ORDER BY id ASC";

        try (Connection conn = Conexao.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                lista.add(u);

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return lista;

    }

    public boolean inserir(User u) {

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, u.getUsername());
            pstmt.setString(2, u.getPassword());
            pstmt.executeUpdate();

            return true;

        } catch (SQLException e) {

            return false;

        }

    }

    public boolean atualizar(User u) {

        if (u.getId() == 1) return false;

        String sql = "UPDATE users SET username = ?, password = ? WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, u.getUsername());
            pstmt.setString(2, u.getPassword());
            pstmt.setInt(3, u.getId());
            pstmt.executeUpdate();

            return true;

        } catch (SQLException e) {

            return false;

        }

    }

    public boolean deletar(int id) {

        if (id == 1) return false;

        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = Conexao.conectar();

             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();

            return true;

        } catch (SQLException e) {

            return false;

        }

    }

}
