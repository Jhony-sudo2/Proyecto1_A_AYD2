import { Component } from '@angular/core';
import { SystemService } from '../Services/System/system.service';
import { SysConfig } from '../interfaces/SystemConfiguration';
import Swal from 'sweetalert2';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-system-config',
  imports: [CommonModule,FormsModule],
  templateUrl: './system-config.component.html',
  styleUrl: './system-config.component.css'
})
export class SystemConfigComponent {
  configuration: SysConfig = {} as SysConfig
  constructor(private service: SystemService) { }

  ngOnInit() {
    this.service.getConfiguration().subscribe({
      next: (response) => { this.configuration = response }
    })
  }

  update() {
    this.service.updateConfiguration(this.configuration).subscribe({
      next: (response) => {
        this.configuration = response;
        Swal.fire({ title: 'OK', text: 'Configuración actualizada', icon: 'success' });
      },
      error: (err) => Swal.fire({ title: 'Error', text: err.error, icon: 'error' })
    });
  }



}
