package edu.uepb.web.biblioteca.business;

import java.util.List;

import org.apache.log4j.Logger;

import edu.uepb.web.biblioteca.dao.AlunoDAOImpl;
import edu.uepb.web.biblioteca.dao.EmprestimoDAOImpl;
import edu.uepb.web.biblioteca.dao.FuncionarioDAOImpl;
import edu.uepb.web.biblioteca.dao.ItemDAOImpl;
import edu.uepb.web.biblioteca.dao.ReservaDAOImpl;
import edu.uepb.web.biblioteca.enums.TipoNivel;
import edu.uepb.web.biblioteca.exception.EmprestimoException;
import edu.uepb.web.biblioteca.model.Aluno;
import edu.uepb.web.biblioteca.model.Emprestimo;
import edu.uepb.web.biblioteca.model.Item;
import edu.uepb.web.biblioteca.model.Reserva;
import edu.uepb.web.biblioteca.utils.BibliotecaDateTime;

/**
 * @autor geovanniovinhas <vinhasgeovannio@gmail.com
 */
public class EmprestimoBusiness {
	private static Logger logger = Logger.getLogger(EmprestimoBusiness.class);
	private FuncionarioDAOImpl funcionarioDAO;
	private ItemDAOImpl itemDAO;
	private AlunoDAOImpl alunoDAO;
	private EmprestimoDAOImpl emprestimoDAO;
	private ReservaDAOImpl reservaDAO;

	/**
	 * O funcionario cadastrar um emprestimo que foi pedido pelo aluno
	 * 
	 * @param idFuncionario
	 * @param idAluno
	 * @param idItem
	 * @return id do emprestimo cadastrado
	 * @throws @throws
	 *             EmprestimoException
	 */
	public int cadastrarEmprestimo(int idFuncionario, int idAluno, int idItem) throws EmprestimoException {
		itemDAO = new ItemDAOImpl();
		Item item = itemDAO.getById(idItem);

		if (item.getQuantidade() < 1) {
			throw new EmprestimoException("O item esta faltando no estoque");
		}

		reservaDAO = new ReservaDAOImpl();

		List<Reserva> listaReserva = reservaDAO.getLista();

		for (Reserva reserva : listaReserva) {
			if (reserva.getAluno().getId() == idAluno && reserva.getItem().getId() == idItem) {
				reservaDAO.remover(reserva);
			}
		}

		funcionarioDAO = new FuncionarioDAOImpl();
		alunoDAO = new AlunoDAOImpl();
		emprestimoDAO = new EmprestimoDAOImpl();
		Emprestimo emprestimo = new Emprestimo();

		Aluno aluno = alunoDAO.getById(idAluno);

		emprestimo.setFuncionario(funcionarioDAO.getById(idFuncionario));
		emprestimo.setAluno(aluno);
		emprestimo.setItem(item);
		emprestimo.setDataCadastrado(BibliotecaDateTime.getDataCadastrado());
		emprestimo.setDataDevolucao(BibliotecaDateTime.getDataDevolucao(aluno.getCurso().getNivel()));

		int idEmprestimo = emprestimoDAO.inserir(emprestimo);

		// Atualizar a quantidade do item
		item.setQuantidade(item.getQuantidade() - 1);
		itemDAO.atualizar(item);

		return idEmprestimo;
	}

	/**
	 * Realizar a devolucao do item emprestado boolean
	 * 
	 * @param idFuncionario @param idAluno @param idEmprestimo @return @throws
	 */
	public boolean devolucaoEmprestimo(int idFuncionario, int idAluno, int idEmprestimo) {
		emprestimoDAO = new EmprestimoDAOImpl();
		itemDAO = new ItemDAOImpl();

		Emprestimo emprestimo = emprestimoDAO.getById(idEmprestimo);
		Item item = itemDAO.getById(emprestimo.getId());

		// aumentar a quantidade do item no estoque
		item.setQuantidade(item.getQuantidade() + 1);
		itemDAO.atualizar(item);

		// atualizar o status do emprestimo como entregado
		emprestimo.setEntregou(true);
		emprestimoDAO.atualizar(emprestimo);

		return true;
	}

	/**
	 * O Funcionario realizar a renovacao do seu emprestimo
	 * 
	 * @param idFuncionario
	 * @param idAluno
	 * @param idEmprestimo
	 * @return boolean
	 * @throws @throws
	 *             EmprestimoException
	 */
	public boolean renovarEmprestimo(int idAluno, int idEmprestimo) throws EmprestimoException {
		emprestimoDAO = new EmprestimoDAOImpl();
		reservaDAO = new ReservaDAOImpl();
		Emprestimo emprestimo = emprestimoDAO.getById(idEmprestimo);

		alunoDAO = new AlunoDAOImpl();
		Aluno aluno = alunoDAO.getById(idEmprestimo);

		// aluno graduacao nao pode renovar mais de uma vez.
		if (aluno.getCurso().getNivel() == TipoNivel.GRADUACAO && emprestimo.getRenovacao() != 1) {
			throw new EmprestimoException("Ja estourou o limite da renovacao!");
		}
		List<Reserva> listaReserva = reservaDAO.getLista();

		for (Reserva reserva : listaReserva) {
			if (emprestimo.getItem().getId() == reserva.getItem().getId()) {
				throw new EmprestimoException("Nao pode realizar a renovacao, o item ja foi reservado por alguem");
			}
		}

		emprestimo.setDataDevolucao(BibliotecaDateTime.getDataDevolucao(aluno.getCurso().getNivel()));
		emprestimo.setRenovacao(emprestimo.getRenovacao() + 1);

		emprestimoDAO.atualizar(emprestimo);

		return true;
	}

}
