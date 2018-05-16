package edu.uepb.web.biblioteca.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.uepb.web.biblioteca.exception.DAOException;
import edu.uepb.web.biblioteca.model.Aluno;
import edu.uepb.web.biblioteca.model.Item;
import edu.uepb.web.biblioteca.model.Reserva;

/**
 * @autor geovanniovinhas <vinhasgeovannio@gmail.com
 *
 *
 */
public class ReservaDAOImpl implements DAO<Reserva> {
	private static Logger logger = Logger.getLogger(Reserva.class);
	private Connection connection;
	private PreparedStatement statement;
	private ResultSet resultSet;
	private AlunoDAOImpl alunoDAO;
	private ItemDAOImpl itemDAO;
	private Aluno aluno;
	private Item item;

	/**
	 * @throws DAOException
	 * @see {@link DAO#get(int)}
	 */
	@Override
	public Reserva get(int id) throws DAOException {
		logger.info("Executa o metodo 'get' do reserva : " + id);
		connection = new Conexao().getConexao();

		String sql = "SELECT * FROM reserva WHERE reserva.id = ?";

		Reserva reserva = null;

		try {
			statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
			resultSet = statement.executeQuery();

			if (resultSet.next()) {
				reserva = new Reserva();
				alunoDAO = new AlunoDAOImpl();
				itemDAO = new ItemDAOImpl();

				aluno = alunoDAO.get(resultSet.getInt(2));
				item = itemDAO.get(resultSet.getInt(3));

				reserva.setAluno(aluno);
				reserva.setItem(item);
				reserva.setDataReservado(resultSet.getString(4));
			}
			statement.close();
		} catch (SQLException e) {
			throw new DAOException(e.getMessage());
		}
		logger.info("O reserva foi selecionado: " + reserva);
		return reserva;
	}

	/**
	 * @throws DAOException
	 * @see {@link DAO#getLista()}
	 */
	@Override
	public List<Reserva> getLista() throws DAOException {
		logger.info("Executa o metodo 'getLista' do reserva");
		connection = new Conexao().getConexao();
		String sql = "SELECT	 * FROM reserva";

		List<Reserva> listaReserva = new ArrayList<>();

		try {
			statement = connection.prepareStatement(sql);
			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				Reserva reserva = new Reserva();
				alunoDAO = new AlunoDAOImpl();
				itemDAO = new ItemDAOImpl();

				aluno = alunoDAO.get(resultSet.getInt(2));
				item = itemDAO.get(resultSet.getInt(3));

				reserva.setAluno(aluno);
				reserva.setItem(item);
				reserva.setDataReservado(resultSet.getString(4));

				listaReserva.add(reserva);
			}
			statement.close();
		} catch (SQLException e) {
			throw new DAOException(e.getMessage());
		}
		logger.info("Pegar as reservas: " + listaReserva.toString());
		return listaReserva;
	}

	/**
	 * @throws DAOException
	 * @see {@link DAO#inserir(Object)}
	 */
	@Override
	public int inserir(Reserva obj) throws DAOException {
		logger.info("Executa o metodo 'inserir' do emprestimo: " + obj);
		int id = -1;
		if (obj != null) {
			connection = new Conexao().getConexao();

			String sql = "INSERT INTO reserva (reserva.aluno_idaluno, reserva.item_iditem, reserva.data_reservado)";

			try {
				statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

				statement.setInt(1, obj.getAluno().getId());
				statement.setInt(2, obj.getItem().getId());
				statement.setString(3, obj.getDataReservado());
				statement.execute();
				resultSet = statement.getGeneratedKeys();
				if (resultSet.next()) {
					id = resultSet.getInt(1);
					obj.setId(id);
				}
				statement.close();
			} catch (SQLException e) {
				throw new DAOException(e.getMessage());
			}

		}
		logger.info("A reserva foi inserido: " + obj);
		return id;
	}

	/**
	 * @throws DAOException
	 * @see {@link DAO#remover(Object)}
	 */
	@Override
	public void remover(Reserva obj) throws DAOException {
		logger.info("Executa o metodo 'remover' reserva : " + obj);
		if (obj != null) {
			connection = new Conexao().getConexao();
			String sql = "DELETE FROM reserva WHERE reserva.id = ?";

			try {
				statement = connection.prepareStatement(sql);
				statement.setInt(1, obj.getId());
				statement.execute();

				statement.close();
				logger.info("A reserva foi removida" + obj);
			} catch (SQLException e) {
				throw new DAOException(e.getMessage());
			}
		}
	}

	@Override
	public void atualizar(Reserva obj) throws DAOException {
		// TODO Auto-generated method stub
	}

	/**
	 * Verificar se o item ainda nao reservado por alguem
	 * 
	 * @throws DAOException
	 * @see {@link DAO#isExiste(Object)}
	 */
	@Override
	public boolean isExiste(Reserva obj) throws DAOException {
		logger.info("Executar metodo 'isExiste' da reserva: " + obj);
		if (obj != null) {
			connection = new Conexao().getConexao();
			String sql = "SELECT * FROM reserva WHERE item_iditem = ?";

			try {
				statement = (PreparedStatement) connection.prepareStatement(sql);
				statement.setInt(1, obj.getItem().getId());
				resultSet = statement.executeQuery();

				if (resultSet.next()) {
					statement.close();
					logger.info("A reserva para esse item ja existe no banco: " + obj);
					return true;
				}
				statement.close();
				logger.info("Esta reserva nao existe no banco: " + obj);
				return false;
			} catch (SQLException e) {
				throw new DAOException(e.getMessage());
			}
		}
		return false;
	}
	
}
