# Pokedex (Android)

Este repositório contém um aplicativo Pokedex exemplo em Kotlin usando Jetpack Compose. O app consome a PokeAPI via Retrofit e exibe uma lista de Pokemons, detalhes, e permite ao usuário montar um time (até 6 pokemons).

<p align="center">
  <img src="https://github.com/user-attachments/assets/b57df211-26f2-49a4-b66c-5962bc3e37a5" alt="print-pokedex" width="360" style="height:auto;" />
</p>
---

## Visão Geral

O app busca dados da PokeAPI, mapeia DTOs para modelos de domínio e exibe as informações com Compose. O projeto inclui:

- Tela principal (lista paginada de Pokemons)
- SearchBar com busca (por paginação ou por endpoint `getPokemonByQuery`)
- Tela de detalhes do Pokémon
- Funcionalidade de montar um time com até 6 pokemons (cards do time exibidos abaixo da searchbar)
- Imagens carregadas com Coil, logging com Timber, rede com Retrofit + Gson

---

## Estrutura do projeto

A estrutura é baseada no padrão MVVM(Model-View-ViewModel).
Principais módulos/pastas (dentro de `app/`):

- `data/` - DTOs, mappers, repositório, fonte de dados (Retrofit)
- `domain/` - modelos (data classes) e lógica independente de infra
- `ui/` - Composables, telas e ViewModels
- `util/` - classes utilitárias, estados (`UiState`), constantes, etc.

---

## Principais bibliotecas e o que fazem

- Retrofit: cliente HTTP tipado. Usado para definir endpoints da PokeAPI e obter DTOs.
- Gson (converter-gson): converte JSON ↔ objetos Kotlin/Java automaticamente.
- Coil: carregamento de imagens (integração com Jetpack Compose via `coil-compose`).
- Timber: logging simplificado (plantar `DebugTree()` em debug). 
- Jetpack Compose: UI declarativa e moderna do Android.

---

## Features

Abaixo estão as funcionalidades principais do aplicativo com descrição e comportamento esperado:

- SearchBar
  - Permite buscar Pokémons por nome ou parte do nome.
  - A barra exibe estados de loading e mensagens de erro quando aplicável.

- Lista principal de Pokémons (Pokedex List)
  - Exibida verticalmente usando `LazyColumn` para scroll eficiente.
  - Cada item é um card com imagem, número, nome e tipos resumidos.
  - A API padrão da PokeAPI retorna páginas (por padrão 20 itens); o app suporta paginação (offset/limit) para carregar mais itens ao alcançar o fim da lista.
  
- Time
  - Permite ao usuário montar um time de até 6 Pokémons.
  - A adição/remoção é feita por long-press no card do Pokémon.
  - Os Pokémons do time aparecem em uma lista horizontal (preferencialmente `LazyRow`) posicionada abaixo da `SearchBar` dentro do header do `LazyColumn` principal, seguida por um separador (Divider) antes da lista principal.

- Tela de Detalhes do Pokémon
  - Ao tocar em um card da lista, o usuário navega para a tela de detalhes.
  - Mostra informações completas: imagem em destaque, número, nome, tipos, stats e descrição (quando disponível).

- Evolução de Pokemon
  - Ao tocar no icone de estrela na tela de detalhes.
  - É trago pela api uma lista de possiveis evoluções do pokemon e é então listada na tela de detalhes.
  - Ao tocar em um card da lista, o usuário navega para a tela de detalhes da evolução escolhida.

- Carregamento de imagens
  - Imagens são carregadas e cacheadas pelo Coil (`coil-compose`), com suporte a placeholder, fallback e crossfade.

- Estados de Loading e Erro
  - Todas as operações de rede expõem estados (`Loading`, `Success`, `Error`) via `UiState` ou `StateFlow`.
  - A UI apresenta `ProgressIndicator` enquanto `Loading` e mensagens amigáveis quando `Error`.

---
