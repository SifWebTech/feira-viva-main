package br.com.feiraviva.config;

import br.com.feiraviva.model.Categoria;
import br.com.feiraviva.model.Produto;
import br.com.feiraviva.repository.CategoriaRepository;
import br.com.feiraviva.repository.ProdutoRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataSeeder {

    @Bean
    ApplicationRunner seed(CategoriaRepository categorias, ProdutoRepository produtos) {
        return args -> {
            if (categorias.count() > 0) return;

            var horta = categorias.save(new Categoria("Horta e Orgânicos", "Verduras, legumes e temperos"));
            var laticinios = categorias.save(new Categoria("Laticínios", "Queijos, iogurtes e manteigas"));
            var temperos = new Categoria("Temperos", "Ervas e especiarias");
            temperos.setCategoriaPai(horta);
            categorias.save(temperos);
            var queijos = new Categoria("Queijos", "Frescos e curados");
            queijos.setCategoriaPai(laticinios);
            categorias.save(queijos);
            var verduras = new Categoria("Verduras", "Folhosas e legumes");
            verduras.setCategoriaPai(horta);
            categorias.save(verduras);

            produtos.save(new Produto("Mel orgânico", new BigDecimal("35.00"), "MEL-001", 12, horta));
            produtos.save(new Produto("Queijo minas", new BigDecimal("28.50"), "QUE-002", 8, laticinios));
            produtos.save(new Produto("Café da serra", new BigDecimal("42.00"), "CAF-003", 20, horta));
            produtos.save(new Produto("Alface crespa",  new BigDecimal("4.50"), "ALF-004", 30, verduras));
            produtos.save(new Produto("Iogurte natural", new BigDecimal("9.90"), "IOG-005", 18, queijos));
        };
    }
}