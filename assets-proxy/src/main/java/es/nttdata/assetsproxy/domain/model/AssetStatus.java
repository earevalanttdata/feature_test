package es.nttdata.assetsproxy.domain.model;

/**
 * Estado del ciclo de vida del Asset dentro del proxy.
 * PENDIENTE de subir, EN PROCESO, COMPLETADO o FALLIDO.
 * DDD: lenguaje ubicuo; evitamos strings mágicos.
 */
public enum AssetStatus {
    PENDING,
    UPLOADING,
    COMPLETED,
    FAILED
}