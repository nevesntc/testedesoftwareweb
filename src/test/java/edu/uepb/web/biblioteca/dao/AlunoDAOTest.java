package edu.uepb.web.biblioteca.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.sql.Connection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.uepb.web.biblioteca.business.FuncionarioBusiness;
import edu.uepb.web.biblioteca.enums.TipoNivel;
import edu.uepb.web.biblioteca.exception.DAOException;
import edu.uepb.web.biblioteca.model.Aluno;
import edu.uepb.web.biblioteca.model.Curso;

/**
 * @autor geovanniovinhas <vinhasgeovannio@gmail.com
 *
 *
 */
public class AlunoDAOTest {
	Aluno aluno1;
	Aluno aluno2;
	Aluno aluno3;
	Aluno aluno4;

	Curso quimica;
	Curso psicologia;
	Curso farmacia;
	Curso matematica;
	Curso relacoes;

	CursoDAOImpl cursoDAO;
	AlunoDAOImpl manager;
	FuncionarioBusiness funService;
	Connection conn;

	@Before
	public void setUp() throws Exception {
		conn = new Conexao().getConexao();
		funService = new FuncionarioBusiness();
		manager = new AlunoDAOImpl();
		cursoDAO = new CursoDAOImpl();

		quimica = new Curso("Quimica", TipoNivel.GRADUACAO, "Exata");
		psicologia = new Curso("Psicologia", TipoNivel.GRADUACAO, "Humanas");
		farmacia = new Curso("Farmacia", TipoNivel.GRADUACAO, "Saude");
		matematica = new Curso("Matematica", TipoNivel.GRADUACAO, "Exatas");
		relacoes = new Curso("Relacoes Int", TipoNivel.GRADUACAO, "Sociais");

		aluno1 = new Aluno("", "435", "34331", "Lula", "Dina", "Brasil", "Sao Paulo", "9893434", null, "2016",
				"2", "dsenr");

		aluno2 = new Aluno("", "wedfd", "0634.401-21", "Kaio", "Dilma", "Brasil", "Jooa Pessoa", "9893434", null,
				"2016", "2", "eriaa");

		aluno3 = new Aluno("", "4s3", "164.002-92", "Herculer", "Jean", "Brasil", "Recife", "9893434", null, "2011",
				"1", "lsjoin");

		aluno4 = new Aluno("", "dsd23", "236002-02", "Hermano", "Jeni", "Timor", "Forta", "98475", null, "2017", "2",
				"BHYD");
	}

	@Test
	public void inserir() throws DAOException {
		int idcurso = cursoDAO.inserir(farmacia);
		aluno1.setCurso(cursoDAO.get(idcurso));

		aluno1.setMatricula(funService.gerarMatricula(aluno1));
		int idaluno = manager.inserir(aluno1);

		if (idaluno < 0) {
			Assert.fail();
		}

	}

	@Test
	public void get() throws DAOException {
		int idCurso = cursoDAO.inserir(psicologia);
		aluno2.setCurso(cursoDAO.get(idCurso));
		
		aluno2.setMatricula(funService.gerarMatricula(aluno2));
		int id = manager.inserir(aluno2);
		assertNotEquals(null, manager.get(id));
	}

	@Test
	public void getLista() throws DAOException {
		List<Aluno> listaAluno = manager.getLista();
		if (listaAluno.size() < 0) {
			Assert.fail();
		}
	}

	@Test
	public void remover() throws DAOException {
		int idCurso = cursoDAO.inserir(matematica);
		aluno3.setCurso(cursoDAO.get(idCurso));
		
		int id = manager.inserir(aluno3);
		aluno3.setMatricula(funService.gerarMatricula(aluno3));
		manager.remover(aluno3);
		assertNotEquals(null, manager.get(id));
	}

	@Test
	public void atualizar() throws DAOException {
		int idCurso = cursoDAO.inserir(quimica);
		aluno4.setCurso(cursoDAO.get(idCurso));

		aluno4.setMatricula(funService.gerarMatricula(aluno4));
		int id = manager.inserir(aluno4);
		aluno4.setId(id);
		aluno4.setEndereco("Canada");

		manager.atualizar(aluno4);

		assertEquals("Canada", manager.get(id).getEndereco());

	}
	
	@Test
	public void isExiste() throws DAOException {
		
		Aluno aluno5 = new Aluno();
		
		int idCurso = cursoDAO.inserir(relacoes);
		aluno5.setCurso(cursoDAO.get(idCurso));
		
		aluno5.setRg("3454");
		aluno5.setCpf("re3454");
		aluno5.setNome("Fred");
		aluno5.setNomeMae("Jina");
		aluno5.setNaturalidade("Holanda");
		aluno5.setEndereco("Rua Hjas");
		aluno5.setTelefone("23454");
		aluno5.setAno("2017");
		aluno5.setPeriodoIngresso("2");
		aluno5.setSenha("erto");
		aluno5.setMatricula(funService.gerarMatricula(aluno5));
		
		assertEquals(false, manager.isExiste(aluno5));
	}

}
