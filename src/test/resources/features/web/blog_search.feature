# language: pt
@web
Funcionalidade: Pesquisa de artigos no Blog do Agi
  Como pessoa usuária do Blog do Agi
  Quero pesquisar artigos pela lupa no canto superior direito
  Para encontrar conteúdos relevantes ou receber um retorno claro quando não houver resultados

  Contexto:
    Dado que acesso a home do Blog do Agi

  Cenário: Buscar artigos com um termo aderente ao conteúdo do blog
    Quando pesquiso pela lupa do cabeçalho pelo termo "emprestimo"
    Então devo ser redirecionado para a página de resultados da busca pelo termo "emprestimo"
    E devo visualizar artigos relacionados ao termo pesquisado
    E o campo de busca deve permanecer preenchido com o termo "emprestimo"

  Cenário: Buscar artigos com um termo inexistente
    Quando pesquiso pela lupa do cabeçalho pelo termo "termoinexistenteagixpto"
    Então devo ser redirecionado para a página de resultados da busca pelo termo "termoinexistenteagixpto"
    E devo visualizar o estado vazio da busca
    E não devo visualizar artigos listados para a pesquisa
    E o campo de busca deve permanecer preenchido com o termo "termoinexistenteagixpto"
