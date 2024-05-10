import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import seaborn as sns


# Função para plotar o gráfico de relação entre tempo de execução e tamanho do array para os algoritmos seriais
def plot_serial_execution_time(df):
    # Filtrar os dados para os algoritmos seriais
    serial_df = df[df["ExecutionType"] == "Serial"]

    # Configurar o plot
    plt.figure(figsize=(10, 6))

    # Iterar por cada algoritmo
    for algo in serial_df["Algorithm"].unique():
        algo_data = serial_df[serial_df["Algorithm"] == algo]
        # Plotar os dados
        plt.plot(
            algo_data["DataSize"], algo_data["AverageTime"], marker="o", label=algo
        )

    # Configurar rótulos e título
    plt.xlabel("Tamanho do Array")
    plt.ylabel("Tempo Médio de Execução (ms)")
    plt.title("Tempo de Execução vs. Tamanho do Array para Algoritmos Seriais")
    plt.legend()
    plt.grid(True)
    plt.tight_layout()

    # Mostrar o plot
    plt.show()


# Função para plotar o gráfico de relação entre tempo de execução, número de threads e tamanho do array para os algoritmos paralelos
def plot_parallel_execution_time(df):
    # Filtrar os dados para incluir apenas execuções paralelas
    parallel_data = df[df["ExecutionType"] == "Parallel"]

    # Configurar a figura do matplotlib
    plt.figure(figsize=(14, 10))

    # Criar um gráfico de barras com seaborn
    sns.barplot(
        data=parallel_data,
        x="DataSize",
        y="AverageTime",
        hue="Algorithm",
        ci=None,
        palette="deep",
    )

    # Melhorar o gráfico
    plt.title("Performance da Execução Paralela")
    plt.xlabel("Tamanho do Array")
    plt.ylabel("Tempo Médio de Execução (ms)")
    plt.legend(title="Algorithm", bbox_to_anchor=(1.05, 1), loc="upper left")
    plt.grid(True)

    # Exibir o gráfico
    plt.tight_layout()
    plt.show()


def plot_algorithm_comparison(data, algorithm_name):
    # Filtrar dados para o algoritmo específico
    algorithm_data = data[data["Algorithm"] == algorithm_name]
    algorithm_data["DetalhesExecucao"] = algorithm_data.apply(
        lambda x: (
            f"{x['ExecutionType']} ({x['ThreadCount']} thread)"
            if x["ExecutionType"] == "Serial"
            else f"{x['ExecutionType']} ({x['ThreadCount']} threads)"
        ),
        axis=1,
    )

    # Pivotar os dados para o gráfico
    pivot_algorithm = algorithm_data.pivot_table(
        values="AverageTime",
        index=["DataSize"],
        columns="DetalhesExecucao",
        aggfunc="mean",
    ).fillna(0)

    # Resetar o índice para facilitar a plotagem
    pivot_algorithm.reset_index(inplace=True)

    # Plotar o gráfico
    plt.figure(figsize=(14, 10))
    sns.barplot(
        data=pivot_algorithm.melt(
            id_vars=["DataSize"], value_vars=pivot_algorithm.columns[1:]
        ),
        x="DataSize",
        y="value",
        hue="DetalhesExecucao",
        ci=None,
        palette="deep",
    )
    plt.title(
        f"Comparação Logarítmica dos Tempos de Execução Serial e Paralela para {algorithm_name}"
    )
    plt.xlabel("Tamanho do Array")
    plt.ylabel("Tempo Médio (ms)")
    plt.yscale("log")
    plt.legend(
        title="Tipo de Execução / Processadores",
        bbox_to_anchor=(1.05, 1),
        loc="upper left",
    )
    plt.grid(True)
    plt.show()


# Função principal
def main():
    # Ler o arquivo CSV para um DataFrame
    df = pd.read_csv("performance_results.csv")

    # Plotar o tempo de execução serial
    plot_serial_execution_time(df)

    # Plotar o tempo de execução paralelo
    plot_parallel_execution_time(df)

    # Gerar gráficos para todos os algoritmos, incluindo Merge Sort
    for algorithm in ["MERGE_SORT", "SELECTION_SORT", "INSERTION_SORT", "BUBBLE_SORT"]:
        plot_algorithm_comparison(df, algorithm)


# Executar a função principal
if __name__ == "__main__":
    main()
