import { Certificate } from '../interfaces/User';
import * as pdfMakeModule from 'pdfmake/build/pdfmake';
import * as pdfFontsModule from 'pdfmake/build/vfs_fonts';
import { Pay } from '../interfaces/Inscription';

const pdfMake = (pdfMakeModule as any).default ?? pdfMakeModule;
const pdfFonts = (pdfFontsModule as any).default ?? pdfFontsModule;
pdfMake.vfs = pdfFonts.vfs;

export class Pdf {
  generarPdfCertificate(c: Certificate): void {
    const docDefinition: any = {
      pageSize: 'LETTER',
      pageOrientation: 'landscape',
      pageMargins: [60, 60, 60, 60],
      background: [
        {
          canvas: [
            {
              type: 'rect', x: 20, y: 20, w: 732, h: 532, r: 12,
              lineColor: '#0ea5e9', lineWidth: 3
            },
            {
              type: 'rect', x: 28, y: 28, w: 716, h: 516, r: 10,
              lineColor: '#6366f1', lineWidth: 1
            },
          ]
        }
      ],
      content: [
        {
          text: '★',
          fontSize: 36,
          color: '#0ea5e9',
          alignment: 'center',
          margin: [0, 10, 0, 0]
        },
        {
          text: 'CERTIFICADO DE PARTICIPACIÓN',
          fontSize: 13,
          bold: true,
          color: '#6366f1',
          alignment: 'center',
          characterSpacing: 4,
          margin: [0, 8, 0, 0]
        },
        {
          canvas: [
            { type: 'line', x1: 160, y1: 0, x2: 612, y2: 0, lineWidth: 1, lineColor: '#0ea5e9' }
          ],
          margin: [0, 10, 0, 10]
        },
        {
          text: 'Este certificado se otorga a',
          fontSize: 12,
          color: '#94a3b8',
          alignment: 'center',
          margin: [0, 0, 0, 6]
        },
        {
          text: `${c.name} ${c.lastName}`,
          fontSize: 32,
          bold: true,
          color: '#1e293b',
          alignment: 'center',
          margin: [0, 0, 0, 6]
        },
        {
          text: c.organizationName,
          fontSize: 11,
          color: '#64748b',
          alignment: 'center',
          italics: true,
          margin: [0, 0, 0, 14]
        },
        {
          text: [
            { text: 'por su participación como  ' },
            { text: c.assitantType, bold: true, color: '#0ea5e9' },
            { text: '  en el congreso' }
          ],
          fontSize: 11,
          color: '#94a3b8',
          alignment: 'center',
          margin: [0, 0, 0, 6]
        },
        {
          text: c.congressName,
          fontSize: 20,
          bold: true,
          color: '#1e293b',
          alignment: 'center',
          margin: [0, 0, 0, 6]
        },
        {
          text: c.locationName,
          fontSize: 10,
          color: '#64748b',
          alignment: 'center',
          italics: true,
          margin: [0, 0, 0, 14]
        },
        {
          canvas: [
            { type: 'line', x1: 160, y1: 0, x2: 612, y2: 0, lineWidth: 1, lineColor: '#6366f1' }
          ],
          margin: [0, 0, 0, 14]
        },
        {
          columns: [
            {
              text: [
                { text: 'Fecha del congreso\n', fontSize: 9, color: '#64748b' },
                {
                  text: `${this.formatDate(c.startDate)} — ${this.formatDate(c.endDate)}`,
                  fontSize: 10, bold: true, color: '#475569'
                }
              ],
              alignment: 'center'
            },
            {
              text: [
                { text: 'Fecha de emisión\n', fontSize: 9, color: '#64748b' },
                {
                  text: this.formatDate(c.date),
                  fontSize: 10, bold: true, color: '#475569'
                }
              ],
              alignment: 'center'
            }
          ]
        }
      ],
      defaultStyle: { font: 'Roboto' }
    };

    pdfMake.createPdf(docDefinition).download(
      `Certificado_${c.name}_${c.congressName}.pdf`
    );
  }

  private formatDate(date: Date): string {
    if (!date) return '';
    const d = new Date(date);
    return d.toLocaleDateString('es-GT', { day: '2-digit', month: 'long', year: 'numeric' });
  }
  generarPdfPago(p: Pay): void {
    const docDefinition: any = {
      pageSize: 'A5',
      pageOrientation: 'portrait',
      pageMargins: [40, 40, 40, 40],
      background: [
        {
          canvas: [
            {
              type: 'rect', x: 10, y: 10, w: 375, h: 575, r: 10,
              lineColor: '#0ea5e9', lineWidth: 2
            },
            {
              type: 'rect', x: 16, y: 16, w: 363, h: 563, r: 8,
              lineColor: '#6366f1', lineWidth: 0.5
            },
          ]
        }
      ],
      content: [
        // Ícono superior
        {
          text: '✦',
          fontSize: 28,
          color: '#0ea5e9',
          alignment: 'center',
          margin: [0, 10, 0, 0]
        },
        // Título
        {
          text: 'COMPROBANTE DE PAGO',
          fontSize: 11,
          bold: true,
          color: '#6366f1',
          alignment: 'center',
          characterSpacing: 3,
          margin: [0, 6, 0, 0]
        },
        // Línea decorativa
        {
          canvas: [
            {
              type: 'line', x1: 40, y1: 0, x2: 315, y2: 0,
              lineWidth: 1, lineColor: '#0ea5e9'
            }
          ],
          margin: [0, 10, 0, 16]
        },
        // ID del pago
        {
          text: `N° ${p.id}`,
          fontSize: 9,
          color: '#64748b',
          alignment: 'center',
          italics: true,
          margin: [0, 0, 0, 16]
        },
        // Congreso
        {
          text: 'Congreso',
          fontSize: 8,
          color: '#64748b',
          alignment: 'center',
          margin: [0, 0, 0, 4]
        },
        {
          text: p.congressName,
          fontSize: 18,
          bold: true,
          color: '#1e293b',
          alignment: 'center',
          margin: [0, 0, 0, 20]
        },
        // Línea separadora
        {
          canvas: [
            {
              type: 'line', x1: 40, y1: 0, x2: 315, y2: 0,
              lineWidth: 0.5, lineColor: '#334155', dash: { length: 4 }
            }
          ],
          margin: [0, 0, 0, 16]
        },
        // Tabla de detalles
        {
          table: {
            widths: ['*', '*'],
            body: [
              [
                { text: 'Participante', fontSize: 8, color: '#64748b', border: [false, false, false, false] },
                { text: 'Fecha de pago', fontSize: 8, color: '#64748b', border: [false, false, false, false] }
              ],
              [
                { text: p.userName, fontSize: 10, bold: true, color: '#1e293b', border: [false, false, false, false], margin: [0, 0, 0, 12] },
                { text: this.formatDate(new Date(p.date)), fontSize: 10, bold: true, color: '#1e293b', border: [false, false, false, false], margin: [0, 0, 0, 12] }
              ],
              [
                { text: 'ID Congreso', fontSize: 8, color: '#64748b', border: [false, false, false, false] },
                { text: 'ID Usuario', fontSize: 8, color: '#64748b', border: [false, false, false, false] }
              ],
              [
                { text: `#${p.congressId}`, fontSize: 10, bold: true, color: '#1e293b', border: [false, false, false, false] },
                { text: `#${p.userId}`, fontSize: 10, bold: true, color: '#1e293b', border: [false, false, false, false] }
              ],
            ]
          },
          margin: [10, 0, 10, 20]
        },
        // Línea decorativa
        {
          canvas: [
            {
              type: 'line', x1: 40, y1: 0, x2: 315, y2: 0,
              lineWidth: 1, lineColor: '#6366f1'
            }
          ],
          margin: [0, 0, 0, 16]
        },
        // Total
        {
          text: 'TOTAL PAGADO',
          fontSize: 9,
          color: '#64748b',
          alignment: 'center',
          characterSpacing: 2,
          margin: [0, 0, 0, 6]
        },
        {
          text: `Q ${p.total.toFixed(2)}`,
          fontSize: 30,
          bold: true,
          color: '#0ea5e9',
          alignment: 'center',
          margin: [0, 0, 0, 20]
        },
        // Línea punteada inferior
        {
          canvas: [
            {
              type: 'line', x1: 40, y1: 0, x2: 315, y2: 0,
              lineWidth: 0.5, lineColor: '#334155', dash: { length: 4 }
            }
          ],
          margin: [0, 0, 0, 12]
        },
        // Footer
        {
          text: 'Este documento es un comprobante oficial de pago',
          fontSize: 7,
          color: '#475569',
          alignment: 'center',
          italics: true
        }
      ],
      defaultStyle: { font: 'Roboto' }
    };

    pdfMake.createPdf(docDefinition).download(
      `Pago_${p.id}_${p.congressName}.pdf`
    );
  }
}