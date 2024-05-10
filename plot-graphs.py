import pandas as pd
import matplotlib.pyplot as plt
import numpy as np


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
    plt.ylabel("Tempo Médio de Execução (ns)")
    plt.title("Tempo de Execução vs. Tamanho do Array para Algoritmos Seriais")
    plt.legend()
    plt.grid(True)
    plt.tight_layout()

    # Mostrar o plot
    plt.show()


# Função para plotar o gráfico de relação entre tempo de execução, número de threads e tamanho do array para os algoritmos paralelos
def plot_parallel_execution_time(df):
    # Filtrar os dados para os algoritmos paralelos
    parallel_df = df[df["ExecutionType"] == "Parallel"]

    # Configurar o plot
    plt.figure(figsize=(12, 8))

    # Definir largura das barras
    bar_width = 0.2

    # Definir posições para as barras
    positions = np.arange(len(parallel_df["DataSize"].unique()))

    # Iterar por cada algoritmo
    for i, algo in enumerate(parallel_df["Algorithm"].unique()):
        algo_data = parallel_df[parallel_df["Algorithm"] == algo]
        # Definir deslocamento para cada conjunto de barras
        offset = (i - 1) * bar_width
        # Iterar por cada número de threads
        for j, threads in enumerate(algo_data["ThreadCount"].unique()):
            threads_data = algo_data[algo_data["ThreadCount"] == threads]
            # Calcular posição para a barra atual
            pos = positions + offset
            # Plotar a barra
            plt.bar(
                pos + j * bar_width,
                threads_data["AverageTime"],
                bar_width,
                label=f"{algo} - Threads: {threads}",
            )

    # Configurar rótulos e título
    plt.xlabel("Tamanho do Array")
    plt.ylabel("Tempo Médio de Execução (ns)")
    plt.title("Tempo de Execução vs. Tamanho do Array para Algoritmos Paralelos")
    plt.xticks(positions + bar_width, parallel_df["DataSize"].unique())
    plt.legend()
    plt.grid(True)
    plt.tight_layout()

    # Mostrar o plot
    plt.show()


# Função principal
def main():
    # Ler o arquivo CSV para um DataFrame
    df = pd.read_csv("performance_results.csv")

    # Plotar o tempo de execução serial
    plot_serial_execution_time(df)

    # Plotar o tempo de execução paralelo
    plot_parallel_execution_time(df)


# Executar a função principal
if __name__ == "__main__":
    main()
